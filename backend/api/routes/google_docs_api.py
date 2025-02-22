import os

from dotenv import load_dotenv
from fastapi import APIRouter, Query
from fastapi.responses import RedirectResponse
from google_auth_oauthlib.flow import Flow
from googleapiclient.discovery import build

google_router = APIRouter(prefix="/google", tags=["google"])

user_sessions = {}  # Stores user session credentials
user_files = {}  # Stores uploaded file IDs

load_dotenv()
CLIENT_ID = os.getenv("GOOGLE_CLIENT_ID")
CLIENT_SECRET = os.getenv("GOOGLE_CLIENT_SECRET")
REDIRECT_URI = os.getenv("GOOGLE_REDIRECT_URI")
SCOPES = os.getenv("GOOGLE_SCOPES")

# Authentication Flow
def get_google_flow():
    return Flow.from_client_config(
        {
            "web": {
                "client_id": CLIENT_ID,
                "client_secret": CLIENT_SECRET,
                "redirect_uris": [REDIRECT_URI],
                "auth_uri": "https://accounts.google.com/o/oauth2/auth",
                "token_uri": "https://oauth2.googleapis.com/token",
            }
        },
        scopes=SCOPES,
    )

@google_router.get("/auth/login")
def login():
    flow = get_google_flow()
    flow.redirect_uri = REDIRECT_URI
    authorization_url, _ = flow.authorization_url(prompt="consent")
    return RedirectResponse(authorization_url)

@google_router.get("/auth/callback")
def auth_callback(code: str):
    flow = get_google_flow()
    flow.redirect_uri = REDIRECT_URI
    flow.fetch_token(code=code)

    credentials = flow.credentials
    user_sessions["user"] = credentials
    return {"message": "Authentication successful! You can now retrieve your documents."}

@google_router.get("/get/docs")
def get_docs():
    if "user" not in user_sessions:
        return {"error": "User not authenticated"}

    credentials = user_sessions["user"]
    if credentials.expired and credentials.refresh_token:
        from google.auth.transport.requests import Request
        credentials.refresh(Request())
        user_sessions["user"] = credentials

    service = build("drive", "v3", credentials=credentials)

    try:
        results = service.files().list(q="mimeType='application/vnd.google-apps.document'", fields="files(id, name)").execute()
        files = results.get("files", [])
    except Exception as e:
        return {"error": str(e)}

    user_files["documents"] = files
    return {"success": "Files have been saved!", "files": files}

def get_doc_content(doc_id: str = Query(..., description="Google Docs file ID")):
    if "user" not in user_sessions:
        return {"error": "User not authenticated"}

    credentials = user_sessions["user"]

    if credentials.expired and credentials.refresh_token:
        from google.auth.transport.requests import Request
        credentials.refresh(Request())
        user_sessions["user"] = credentials

    # Use Google Docs API (docs.v1) instead of Drive API
    service = build("docs", "v1", credentials=credentials)

    try:
        document = service.documents().get(documentId=doc_id).execute()
        content = parse_google_doc_content(document)
        return content
    except Exception as e:
        return {"error": str(e)}

def parse_google_doc_content(document):
    """
    Extracts and formats text content from a Google Docs document.
    """
    content = ""
    for element in document.get("body", {}).get("content", []):
        if "paragraph" in element:
            for paragraph_element in element["paragraph"]["elements"]:
                if "textRun" in paragraph_element:
                    content += paragraph_element["textRun"]["content"]
    return content.strip()
