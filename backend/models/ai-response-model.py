import openai
import time

openai.api_key = "your-api-key-here"  # Alternatively, use os.getenv("OPENAI_API_KEY")

assistant = openai.beta.assistants.create(
    name="Paper Grader",
    instructions="You are a strict but fair academic paper grader. Analyze the uploaded files and evaluate the student's work based on clarity, argument strength, grammar, and structure. Provide constructive feedback and a grade out of 100.",
    model="gpt-4-turbo"
)
thread = openai.beta.threads.create()


def upload_user_file(file_path):
    with open(file_path, "rb") as f:
        file = openai.files.create(
            file=f,
            purpose="assistants"
        )
    return file.id

def grade_inputted_text(text):
    rubric = upload_user_file("rubric.pdf")
    example = upload_user_file("example.pdf")
    #Get text graded based on the files
    openai.beta.threads.messages.create(
        thread_id=thread.id,
        role="user",
        content="Grade the inputted text using the supporting rubric and example documents /n" + text,
        attachments=[
            {"file_id": rubric},
            {"file_id": example}
        ]
    )
    request=openai.beta.threads.runs.create(
        thread_id=thread.id,
        assistant_id=assistant.id
    )
    return request

def retrieve_response(thread_id, run_id):
    while True:
        request_status = openai.beta.threads.runs.retrieve(thread_id, run_id)
        if request_status.status == "completed":
            break
        print("Waiting for grading to complete...")
        time.sleep(2)

    messages = openai.beta.threads.messages.list(thread_id)
    return messages.data[0].content



# Example usage
user_input = "Explain the theory of relativity in simple terms."
response = grade_inputted_text(user_input)
feedback = retrieve_response(thread.id, response.id)
print(feedback)

