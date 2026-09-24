export const API_BASE_URL =
  import.meta.env.VITE_API_BASE_URL ??
  "/api";

export const API_ENDPOINTS = {
  auth: {
    login: "/auth/login",
  },

  user: {
    register: "/user",
    current: "/user",
  },

  task: {
    list: "/tasks",

    byId: (id: number) =>
      `/tasks/${id}`,
  },
};
