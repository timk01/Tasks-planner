import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { register } from "../../api/authApi";
import styles from "./RegisterPage.module.css";

export default function RegisterPage() {
  const navigate = useNavigate();

  const [email, setEmail] =
    useState("");

  const [password, setPassword] =
    useState("");

  const [confirmPassword, setConfirmPassword] =
    useState("");

  const [error, setError] =
    useState("");

  const [loading, setLoading] =
    useState(false);

  async function handleSubmit(
    event: React.FormEvent
  ) {
    event.preventDefault();

    if (password !== confirmPassword) {
      setError(
        "Passwords do not match"
      );
      return;
    }

    try {
      setError("");
      setLoading(true);

      const authResponse = await register({
        email,
        password,
        passwordConfirmation: confirmPassword,
      });

      localStorage.setItem(
        "token",
        authResponse.token
      );

      navigate("/tasks", {
        replace: true,
      });

    } catch (err) {
      setError(
        err instanceof Error
          ? err.message
          : "Registration failed"
      );

    } finally {
      setLoading(false);
    }
  }

  return (
    <div className={styles.page}>
      <div className={styles.card}>

        <div className={styles.header}>
          <h1>
            Create account
          </h1>

          <p>
            Start managing your tasks today
          </p>
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
              placeholder="Enter your email"
              onChange={(event) =>
                setEmail(
                  event.target.value
                )
              }
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
              placeholder="Create a password"
              minLength={5}
              maxLength={20}
              onChange={(event) =>
                setPassword(
                  event.target.value
                )
              }
              required
            />
          </div>

          <div className={styles.field}>
            <label htmlFor="confirmPassword">
              Confirm password
            </label>

            <input
              id="confirmPassword"
              type="password"
              value={confirmPassword}
              placeholder="Repeat your password"
              minLength={5}
              maxLength={20}
              onChange={(event) =>
                setConfirmPassword(
                  event.target.value
                )
              }
              required
            />
          </div>

          <button
            className={styles.primaryButton}
            type="submit"
            disabled={loading}
          >
            {loading
              ? "Creating account..."
              : "Create account"}
          </button>

          <button
            className={styles.secondaryButton}
            type="button"
            onClick={() =>
              navigate("/login")
            }
          >
            Already have an account
          </button>

        </form>

      </div>
    </div>
  );
}
