import api from "../api.js";
import { useState } from "react";

export default function FileUploader() {
  const [file, setFile] = useState(null);
  const [status, setStatus] = useState("idle");
  const [uploadProgress, setUploadProgress] = useState(0);
  const [uploadType, setUploadType] = useState("example"); // Default selection

  function handleFileChange(e) {
    if (e.target.files.length > 0) {
      setFile(e.target.files[0]);
    }
  }

  async function handleFileUpload() {
    if (!file) return;

    setStatus("uploading");
    setUploadProgress(0);

    const formData = new FormData();
    formData.append("upload_file", file);

    const endpoint = uploadType === "example" ? "/upload/resource/example" : "/upload/resource/markscheme";

    try {
      await api.post(endpoint, formData, {
        headers: {
          "Content-Type": "multipart/form-data",
        },
        onUploadProgress: (progressEvent) => {
          const progress = progressEvent.total
            ? Math.round((progressEvent.loaded * 100) / progressEvent.total)
            : 0;
          setUploadProgress(progress);
        },
      });

      setStatus("success");
      setUploadProgress(100);
    } catch {
      setStatus("error");
      setUploadProgress(0);
    }
  }

  return (
    <div className="space-y-2">
      <div className="flex space-x-4">
        <button
          className={`px-4 py-2 border ${uploadType === "example" ? "bg-blue-600 text-white" : "bg-gray-200"}`}
          onClick={() => setUploadType("example")}
        >
          Upload Example File
        </button>
        <button
          className={`px-4 py-2 border ${uploadType === "rubric" ? "bg-blue-600 text-white" : "bg-gray-200"}`}
          onClick={() => setUploadType("rubric")}
        >
          Upload Rubric File
        </button>
      </div>

      <input type="file" onChange={handleFileChange} />

      {file && (
        <div className="mb-4 text-sm">
          <p>File name: {file.name}</p>
          <p>Size: {(file.size / 1024).toFixed(2)} KB</p>
          <p>Type: {file.type}</p>
        </div>
      )}

      {status === "uploading" && (
        <div className="space-y-2">
          <div className="h-2.5 w-full rounded-full bg-gray-200">
            <div
              className="h-2.5 rounded-full bg-blue-600 transition-all duration-300"
              style={{ width: `${uploadProgress}%` }}
            ></div>
          </div>
          <p className="text-sm text-gray-600">{uploadProgress}% uploaded</p>
        </div>
      )}

      {file && status !== "uploading" && (
        <button onClick={handleFileUpload} className="px-4 py-2 bg-green-500 text-white rounded">
          Upload
        </button>
      )}

      {status === "success" && (
        <p className="text-sm text-green-600">File uploaded successfully!</p>
      )}

      {status === "error" && (
        <p className="text-sm text-red-600">Upload failed. Please try again.</p>
      )}
    </div>
  );
}
