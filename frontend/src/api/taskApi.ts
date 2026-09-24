import {
  API_BASE_URL,
  API_ENDPOINTS,
} from "../config/api";

export type TaskStatus =
  | "CREATED"
  | "IN_PROCESS"
  | "FINISHED";

export interface Task {
  taskId: number;
  header: string;
  text: string;
  status: TaskStatus;
  finishedAt: string | null;
  ownerId: number;
}

export interface CreateTaskRequest {
  header: string;
  text: string;
}

export interface UpdateTaskRequest {
  header?: string;
  text?: string;
  status?: TaskStatus;
}

function getAuthHeaders() {
  const token = localStorage.getItem("token");

  if (!token) {
    throw new Error("User is not authenticated");
  }

  return {
    "Content-Type": "application/json",
    Authorization: `Bearer ${token}`,
  };
}

async function handleResponse<T>(
  response: Response
): Promise<T> {
  if (!response.ok) {
    const error = await response
      .json()
      .catch(() => ({
        message: "Request failed",
      }));

    throw new Error(
      error.message || "Request failed"
    );
  }

  const contentType =
    response.headers.get("content-type");

  if (
    !contentType ||
    !contentType.includes("application/json")
  ) {
    return undefined as T;
  }

  return response.json();
}

export async function getTasks(): Promise<Task[]> {
  const response = await fetch(
    `${API_BASE_URL}${API_ENDPOINTS.task.list}`,
    {
      method: "GET",
      headers: getAuthHeaders(),
    }
  );

  return handleResponse<Task[]>(response);
}

export async function createTask(
  data: CreateTaskRequest
): Promise<Task> {
  const response = await fetch(
    `${API_BASE_URL}${API_ENDPOINTS.task.list}`,
    {
      method: "POST",
      headers: getAuthHeaders(),
      body: JSON.stringify(data),
    }
  );

  return handleResponse<Task>(response);
}

export async function updateTask(
  id: number,
  data: UpdateTaskRequest
): Promise<Task> {
  const response = await fetch(
    `${API_BASE_URL}${API_ENDPOINTS.task.byId(id)}`,
    {
      method: "PATCH",
      headers: getAuthHeaders(),
      body: JSON.stringify(data),
    }
  );

  return handleResponse<Task>(response);
}

export async function deleteTask(
  id: number
): Promise<void> {
  const response = await fetch(
    `${API_BASE_URL}${API_ENDPOINTS.task.byId(id)}`,
    {
      method: "DELETE",
      headers: getAuthHeaders(),
    }
  );

  return handleResponse<void>(response);
}
