import os
import uvicorn
from supabase import create_client, Client
from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel
from typing import List

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
    "http://localhost:3000"
]

app.add_middleware(
    CORSMiddleware,
    allow_origins=origins,
    allow_credentials = True,
    allow_methods=["*"],
    allow_headers=["*"]
)

memory_db = {"responses": []}

@app.get("/responses", response_model=Responses)
def get_responses():
    return Responses(responses=memory_db["responses"])

@app.post("/responses", response_model=Response)
def add_response(response : Response):
    memory_db["responses"].append(response)
    return response

if __name__ == "__main__":
    uvicorn.run(app, host="0.0.0.0", port=8000)
