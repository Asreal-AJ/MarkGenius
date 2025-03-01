
import os
import openai
import requests
from dotenv import load_dotenv

load_dotenv()
openai.api_key = os.getenv("OPENAI_API_KEY")
google_api_key = os.getenv("GOOGLE_API_KEY")
search_engine_id = os.getenv("SEARCH_ENGINE_ID")

def search_resource(topic, resource):
    topic_keywords = generate_keywords(topic)
    google_search_query = f"{topic} {' '.join(topic_keywords)} {resource} site:.edu OR site:.org filetype:pdf"

    search_url = f"https://www.googleapis.com/customsearch/v1?q={google_search_query}&key={google_api_key}&cx={search_engine_id}"

    response = requests.get(search_url)
    if response.status_code == 200:
        results = response.json().get("items", [])
        return [{"title": r["title"], "link": r["link"]} for r in results]
    return None


def generate_keywords(topic):
    gpt_response = openai.ChatCompletion.create(
        model="gpt-4",
        messages=[
            {
                "role": "system",
                "content": "Extract 5 keywords that best represents the given topic.",
            },
            {
                "role": "user",
                "content": topic,
            }
        ]
    )
    keywords = gpt_response["choices"][0]["message"]["content"]
    return keywords.split(", ")