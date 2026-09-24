import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { login } from "../../api/authApi";
import styles from "./LoginPage.module.css";

export default function LoginPage() {
  const navigate = useNavigate();

  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");

  async function handleSubmit(event: React.FormEvent) {
    event.preventDefault();

    try {
      setError("");

      const response = await login({
        email,
        password,
      });

      localStorage.setItem(
        "token",
        response.token
      );

      navigate("/tasks");

    } catch (err) {
      setError(
        err instanceof Error
          ? err.message
          : "Login failed"
      );
    }
  }

  return (
    <div className={styles.page}>
      <div className={styles.card}>
        <div className={styles.header}>
          <h1>Welcome back</h1>
          <p>Sign in to manage your tasks</p>
        </div>

        <form onSubmit={handleSubmit}>
          {error && (
            <div className={styles.error}>
              {error}
            </div>
          )}

          <div className={styles.field}>
            <label htmlFor="email">
              Email
            </label>

            <input
              id="email"
              type="email"
              value={email}
              onChange={(event) =>
                setEmail(event.target.value)
              }
              placeholder="Enter your email"
              required
            />
          </div>

          <div className={styles.field}>
            <label htmlFor="password">
              Password
            </label>

            <input
              id="password"
              type="password"
              value={password}
              onChange={(event) =>
                setPassword(event.target.value)
              }
              placeholder="Enter your password"
              minLength={5}
              maxLength={20}
              required
            />
          </div>

          <button
            className={styles.primaryButton}
            type="submit"
          >
            Sign in
          </button>

          <button
            type="button"
            className={styles.secondaryButton}
            onClick={() => navigate("/register")}
          >
            Create an account
          </button>
        </form>
      </div>
    </div>
  );
}
