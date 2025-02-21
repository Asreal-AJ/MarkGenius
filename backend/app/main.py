from fastapi import FastAPI, UploadFile, File
from fastapi.middleware.cors import CORSMiddleware
from backend.api.routes.google_docs_api import google_router

app = FastAPI(title='MarkGenius')
app.include_router(google_router)

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

@app.get("/")
def index():
    return {"status": "MarkGenius is running"}