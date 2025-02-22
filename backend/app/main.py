from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware

from backend.api.routes.google_docs_api import google_router, user_files, get_doc_content, parse_google_doc_content
from backend.api.routes.resource_upload_api import file_upload_router, uploaded_files
from backend.models.ai_response_model import grade_inputted_text, get_feedback

app = FastAPI(title='MarkGenius')
app.include_router(google_router)
app.include_router(file_upload_router)

origins = [
    "http://localhost:5173"
]

app.add_middleware(
    CORSMiddleware,
    allow_origins=origins,
    allow_credentials = True,
    allow_methods=["*"],
    allow_headers=["*"]
)
print("✅ CORS middleware added!")

@app.get("/")
def index():
    return {"status": "MarkGenius is running"}

@app.get("/grade")
def run_grade_function():
    if "documents" not in user_files:
        return {"error": "No user files"}
    # Example usage
    user_documents = user_files["documents"]
    user_document = next((file for file in user_documents if file["name"] == "TOK"), None)
    user_input_text_id = user_document["id"]
    example_file = uploaded_files["example"][0]
    rubric_file = uploaded_files["markscheme"][0]

    #Get contents of the document
    text_content = get_doc_content(user_input_text_id)

    # Grade the inputted text
    response = grade_inputted_text(text_content, example_file, rubric_file)

    # Retrieve feedback
    feedback = get_feedback(response.id)
    print(feedback)