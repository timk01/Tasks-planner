import { useState } from "react";
import type { CreateTaskRequest } from "../api/taskApi";
import styles from "./TaskForm.module.css";

interface Props {
  onSubmit: (
    data: CreateTaskRequest
  ) => void;

  onCancel: () => void;
}

export default function TaskForm({
  onSubmit,
  onCancel,
}: Props) {
  const [header, setHeader] =
    useState("");

  const [text, setText] =
    useState("");

  function handleSubmit(
    event: React.FormEvent
  ) {
    event.preventDefault();

    const normalizedHeader = header.trim();
    const normalizedText = text.trim();

    if (
      normalizedHeader.length < 5 ||
      !normalizedText
    ) {
      return;
    }

    onSubmit({
      header: normalizedHeader,
      text: normalizedText,
    });

    setHeader("");
    setText("");
  }

  return (
    <form
      className={styles.form}
      onSubmit={handleSubmit}
    >

      <div className={styles.header}>
        <h2>
          Create task
        </h2>
      </div>

      <div className={styles.field}>
        <label htmlFor="header">
          Title
        </label>

        <input
          id="header"
          type="text"
          value={header}
          placeholder="Enter task title"
          minLength={5}
          maxLength={60}
          onChange={(event) =>
            setHeader(
              event.target.value
            )
          }
          required
        />
      </div>

      <div className={styles.field}>
        <label htmlFor="text">
          Description
        </label>

        <textarea
          id="text"
          value={text}
          placeholder="Add details about this task"
          onChange={(event) =>
            setText(
              event.target.value
            )
          }
          required
        />
      </div>

      <div className={styles.actions}>

        <button
          type="button"
          className={styles.cancelButton}
          onClick={onCancel}
        >
          Cancel
        </button>

        <button
          type="submit"
          className={styles.createButton}
        >
          Create task
        </button>

      </div>

    </form>
  );
}
