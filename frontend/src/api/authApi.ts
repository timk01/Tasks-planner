import {
  API_BASE_URL,
  API_ENDPOINTS,
} from "../config/api";

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  email: string;
  password: string;
  passwordConfirmation: string;
}

export interface User {
  id: number;
  email: string;
}

export interface AuthResponse {
  user: User;
  token: string;
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

async function handleAuthResponse(
  response: Response
): Promise<AuthResponse> {
  const user = await handleResponse<User>(response);
  const authorization =
    response.headers.get("Authorization");

  if (
    !authorization ||
    !authorization.startsWith("Bearer ")
  ) {
    throw new Error(
      "Authentication token was not received"
    );
  }

  return {
    user,
    token: authorization.substring(7),
  };
}

export async function login(
  data: LoginRequest
): Promise<AuthResponse> {
  const response = await fetch(
    `${API_BASE_URL}${API_ENDPOINTS.auth.login}`,
    {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(data),
    }
  );

  return handleAuthResponse(response);
}

export async function register(
  data: RegisterRequest
): Promise<AuthResponse> {
  const response = await fetch(
    `${API_BASE_URL}${API_ENDPOINTS.user.register}`,
    {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(data),
    }
  );

  return handleAuthResponse(response);
}

export async function getCurrentUser(
  token: string
): Promise<User> {
  const response = await fetch(
    `${API_BASE_URL}${API_ENDPOINTS.user.current}`,
    {
      method: "GET",
      headers: {
        Authorization: `Bearer ${token}`,
      },
    }
  );

  return handleResponse<User>(response);
}
