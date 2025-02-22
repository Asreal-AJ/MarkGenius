import os
from fastapi import APIRouter, UploadFile, File, HTTPException
from fastapi.responses import FileResponse

# Define router
file_upload_router = APIRouter(prefix="/upload", tags=["upload"])

# Directory to store files
UPLOAD_DIRECTORY = "uploads"
os.makedirs(UPLOAD_DIRECTORY, exist_ok=True)  # Ensure directory exists

# Dictionary to store filenames (consider using a database for production)
uploaded_files = {
    "example": [],
    "markscheme": []
}

# Upload file endpoint
@file_upload_router.post("/resource/example")
async def handle_example_upload(upload_file: UploadFile = File(...)):
    file_path = os.path.join(UPLOAD_DIRECTORY, upload_file.filename)

    with open(file_path, "wb") as buffer:
        buffer.write(await upload_file.read())  # Save file

    uploaded_files["example"].append(upload_file.filename)  # Store filename reference
    return {"message": "File uploaded successfully", "filename": upload_file.filename}


@file_upload_router.post("/resource/markscheme")
async def handle_markscheme_upload(upload_file: UploadFile = File(...)):
    file_path = os.path.join(UPLOAD_DIRECTORY, upload_file.filename)

    with open(file_path, "wb") as buffer:
        buffer.write(await upload_file.read())  # Save file

    uploaded_files["markscheme"].append(upload_file.filename)  # Store filename reference
    return {"message": "File uploaded successfully", "filename": upload_file.filename}


# Retrieve uploaded files list
@file_upload_router.get("/resource/list/{category}")
async def list_uploaded_files(category: str):
    if category not in uploaded_files:
        raise HTTPException(status_code=404, detail="Category not found")

    return {"category": category, "files": uploaded_files[category]}


# Retrieve specific file
@file_upload_router.get("/resource/get/{filename}")
async def get_uploaded_file(filename: str):
    file_path = os.path.join(UPLOAD_DIRECTORY, filename)

    if not os.path.exists(file_path):
        raise HTTPException(status_code=404, detail="File not found")

    return FileResponse(file_path, filename=filename)
