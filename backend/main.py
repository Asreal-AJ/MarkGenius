import uvicorn


#uvicorn main:app --reload --host 0.0.0.0 --port 8000 to run server

# url: str = os.environ.get("SUPABASE_URL")
# key: str = os.environ.get("SUPABASE_KEY")
# supabase: Client = create_client(url, key)


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
    uvicorn.run("backend.app.main:app", host="0.0.0.0", port=8000, reload=True)
