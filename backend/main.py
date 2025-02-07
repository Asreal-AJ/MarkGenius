import os
import uvicorn
from supabase import create_client, Client
from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel
from typing import List
from google_auth_oauthlib.flow import Flow
from googleapiclient.discovery import build
from fastapi.responses import RedirectResponse
import os


#uvicorn main:app --reload --host 0.0.0.0 --port 8000 to run server

# url: str = os.environ.get("SUPABASE_URL")
# key: str = os.environ.get("SUPABASE_KEY")
# supabase: Client = create_client(url, key)

##Boilerplater code to test out FastAPI
class Response(BaseModel):
    message: str

class Responses(BaseModel):
    responses: List[Response]

app = FastAPI()

origins = [
    "http://localhost:8000"
]

app.add_middleware(
    CORSMiddleware,
    allow_origins=origins,
    allow_credentials = True,
    allow_methods=["*"],
    allow_headers=["*"]
)
print("✅ CORS middleware added!")

user_sessions = {}

CLIENT_ID = os.getenv("GOOGLE_CLIENT_ID")
CLIENT_SECRET = os.getenv("GOOGLE_CLIENT_SECRET")
REDIRECT_URI = os.getenv("GOOGLE_REDIRECT_URI")
SCOPES = os.getenv("GOOGLE_SCOPES").split()


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

#http://localhost:8000/auth/login - to login in to your google account

@app.get("/auth/login")
def login():
    flow = get_google_flow()
    flow.redirect_uri = REDIRECT_URI
    authorization_url, _ = flow.authorization_url(prompt="consent")
    return RedirectResponse(authorization_url)


#call back url that just saves the users credentials
@app.get("/auth/callback")
def auth_callback(code: str):
    flow = get_google_flow()
    flow.redirect_uri = REDIRECT_URI
    flow.fetch_token(code=code)

    credentials = flow.credentials
    user_sessions["user"] = credentials
    print(user_sessions)
    return {"message": "Authentication successful! You can now retrieve your documents."}


#http://localhost:8000/docs - to get first 10 google docs documents
@app.get("/get/docs")
def get_docs():  
    if "user" not in user_sessions:
        print("User not authenticated!")
        return {"error": "User not authenticated"}

    credentials = user_sessions.get("user")

    if not credentials:
        print("Credentials missing!")
        return {"error": "User session does not have credentials"}

    # Check if credentials are valid
    if credentials.expired and credentials.refresh_token:
        from google.auth.transport.requests import Request
        credentials.refresh(Request())
        user_sessions["user"] = credentials  # Save refreshed credentials

    service = build("drive", "v3", credentials=credentials)
    
    try:
        results = service.files().list(q="mimeType='application/vnd.google-apps.document'", fields="files(id, name)").execute()
        files = results.get("files", [])
    except Exception as e:
        return {"error": str(e)}

    return {"documents": files}



"""
memory_db = {"responses": []}

@app.get("/responses", response_model=Responses)
def get_responses():
    return Responses(responses=memory_db["responses"])

@app.post("/responses", response_model=Response)
def add_response(response : Response):
    memory_db["responses"].append(response)
    return response
"""

if __name__ == "__main__":
    uvicorn.run(app, host="0.0.0.0", port=8000)
