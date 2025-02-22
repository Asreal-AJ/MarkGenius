import openai
import os
import time
from dotenv import load_dotenv

from backend.api.routes.resource_upload_api import uploaded_files

load_dotenv()
openai.api_key = os.getenv("OPENAI_API_KEY")

assistant = openai.beta.assistants.retrieve("asst_zJTTDRuNvOpkhd9WY3An20eI")

thread = openai.beta.threads.create()

def upload_user_file(file_path):
    """Upload a file to OpenAI for use in grading."""
    with open(file_path, "rb") as f:
        file = openai.files.create(file=f, purpose="assistants")
    return file.id


def grade_inputted_text(text, example_filename, rubric_filename):
    if rubric_filename not in uploaded_files["markscheme"] or example_filename not in uploaded_files["example"]:
        return {"error": "Required files not uploaded. Please upload rubric and example documents."}

    # Ensure the uploaded files exist in the uploads directory
    example_path = f"uploads/{example_filename}"
    rubric_path = f"uploads/{rubric_filename}"

    if not os.path.exists(example_path) or not os.path.exists(rubric_path):
        raise FileNotFoundError("Example or rubric file not found!")

    # Upload files to OpenAI
    example_file_id = upload_user_file(example_path)
    rubric_file_id = upload_user_file(rubric_path)

    # Send grading request to OpenAI
    openai.beta.threads.messages.create(
        thread_id=thread.id,
        role="user",
        content=f"Grade the following text using the rubric and example document:\n\n{text}",
        attachments=[
            {"file_id": rubric_file_id, "tools": [{"type": "file_search"}]},
            {"file_id": example_file_id, "tools": [{"type": "file_search"}]}
        ]
    )

    request = openai.beta.threads.runs.create(
        thread_id=thread.id,
        assistant_id=assistant.id
    )

    return request


def retrieve_response(thread_id, run_id):
    """Retrieve grading results from OpenAI, with error handling for failures."""
    max_retries = 10  # Limit retries to avoid infinite loops
    retry_count = 0

    while retry_count < max_retries:
        try:
            request_status = openai.beta.threads.runs.retrieve(run_id=run_id, thread_id=thread_id)

            if request_status.status == "completed":
                messages = openai.beta.threads.messages.list(thread_id)
                return messages.data[0].content

            if request_status.status == "failed":
                print("Grading process failed.")
                return None  # Indicate failure

            print("Waiting for grading to complete...")
            time.sleep(2)
            retry_count += 1

        except Exception as e:
            print(f"Error retrieving response: {e}")
            time.sleep(2)
            retry_count += 1

    print("Max retries reached. Could not retrieve response.")
    return None  # Indicate failure after max retries

def get_feedback(response_id):
    return retrieve_response(thread.id, response_id)


