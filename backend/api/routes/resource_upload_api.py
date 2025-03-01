import os
from fastapi import APIRouter, UploadFile, File, HTTPException, Query
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

@file_upload_router.post("/resource/{resource}")
async def upload_resource(upload_file: UploadFile = File(...), resource: str = Query(..., description="Resource ID")):
    try:
        if resource == "markscheme":
            await handle_resource_upload(upload_file, "markscheme")
        if resource == "example":
            await handle_resource_upload(upload_file, "example")
    except Exception as e:
        return {"error": str(e)}
    return {"message": "File uploaded successfully", "filename": upload_file.filename}


# Retrieve uploaded files list
# @file_upload_router.get("/resource/list/{category}")
# async def list_uploaded_files(category: str):
#     if category not in uploaded_files:
#         raise HTTPException(status_code=404, detail="Category not found")
#
#     return {"category": category, "files": uploaded_files[category]}
#
# # Retrieve specific file
# @file_upload_router.get("/resource/get/{filename}")
# async def get_uploaded_file(filename: str):
#     file_path = os.path.join(UPLOAD_DIRECTORY, filename)
#
#     if not os.path.exists(file_path):
#         raise HTTPException(status_code=404, detail="File not found")
#
#     return FileResponse(file_path, filename=filename)

async def handle_resource_upload(upload_file: UploadFile = File(...), resource_type: str = Query(..., description="Resource type")):
    file_path = os.path.join(UPLOAD_DIRECTORY, upload_file.filename)

    with open(file_path, "wb") as buffer:
        buffer.write(await upload_file.read())  # Save file

    uploaded_files[resource_type].append(upload_file.filename)  # Store filename reference
