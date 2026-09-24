import { useState } from "react";
import styles from "./TaskCard.module.css";
import type {
  Task,
  TaskStatus,
  UpdateTaskRequest,
} from "../api/taskApi";

interface Props {
  task: Task;

  onUpdate: (
    id: number,
    data: UpdateTaskRequest
  ) => Promise<void>;

  onDelete: (
    id: number
  ) => void;
}

function getStatusLabel(status: Task["status"]) {
  switch (status) {
    case "CREATED":
      return "Created";
    case "IN_PROCESS":
      return "In progress";
    case "FINISHED":
      return "Finished";
  }
}

function getAvailableStatuses(status: TaskStatus): TaskStatus[] {
  switch (status) {
    case "CREATED":
      return ["IN_PROCESS", "FINISHED"];
    case "IN_PROCESS":
      return ["FINISHED"];
    case "FINISHED":
      return [];
  }
}

export default function TaskCard({
  task,
  onUpdate,
  onDelete,
}: Props) {
  const [editing, setEditing] =
    useState(false);

  const [confirmDelete, setConfirmDelete] =
    useState(false);

  const [header, setHeader] =
    useState(task.header);

  const [text, setText] =
    useState(task.text);

  const [nextStatus, setNextStatus] =
    useState<TaskStatus | "">("");

  const [validationError, setValidationError] =
    useState("");

  const completed =
    task.status === "FINISHED";

  const availableStatuses =
    getAvailableStatuses(task.status);

  function beginEditing() {
    setHeader(task.header);
    setText(task.text);
    setNextStatus("");
    setValidationError("");
    setEditing(true);
  }

  async function handleConfirmEdit() {
    const normalizedHeader = header.trim();
    const normalizedText = text.trim();

    if (normalizedHeader.length < 5) {
      setValidationError(
        "Title must contain at least 5 characters."
      );
      return;
    }

    if (normalizedHeader.length > 60) {
      setValidationError(
        "Title must contain no more than 60 characters."
      );
      return;
    }

    if (!normalizedText) {
      setValidationError(
        "Description must not be empty."
      );
      return;
    }

    setValidationError("");

    const data: UpdateTaskRequest = {
      header: normalizedHeader,
      text: normalizedText,
    };

    if (nextStatus) {
      data.status = nextStatus;
    }

    try {
      await onUpdate(task.taskId, data);
      setEditing(false);
      setNextStatus("");
    } catch (err) {
      setValidationError(
        err instanceof Error
          ? err.message
          : "Failed to update task"
      );
    }
  }

  function handleDelete() {
    onDelete(task.taskId);
    setConfirmDelete(false);
  }

  return (
    <div
      className={`${styles.card} ${
        completed
          ? styles.completed
          : ""
      }`}
    >

      <div className={styles.content}>

        <div
          className={`${styles.check} ${
            completed
              ? styles.checked
              : ""
          }`}
          aria-label={
            completed
              ? "Task finished"
              : "Task not finished"
          }
          title={
            completed
              ? "Task finished"
              : "Task not finished"
          }
        >
          {completed && "✓"}
        </div>

        <div className={styles.text}>
          <div className={styles.titleRow}>
            {editing ? (
              <input
                className={styles.titleInput}
                value={header}
                onChange={(event) => {
                  setHeader(event.target.value);
                  setValidationError("");
                }}
              />
            ) : (
              <h3>{task.header}</h3>
            )}

            <span className={styles.status}>
              {getStatusLabel(task.status)}
            </span>
          </div>

          {editing ? (
            <>
              <div className={styles.fieldHint}>
                Title: 5–60 characters ({header.trim().length}/60)
              </div>

              <textarea
                className={styles.descriptionInput}
                value={text}
                onChange={(event) => {
                  setText(
                    event.target.value
                  );
                  setValidationError("");
                }}
              />

              {availableStatuses.length > 0 && (
                <div className={styles.statusEditor}>
                  <label htmlFor={`status-${task.taskId}`}>
                    Change status
                  </label>

                  <select
                    id={`status-${task.taskId}`}
                    value={nextStatus}
                    onChange={(event) => {
                      setNextStatus(
                        event.target.value as TaskStatus | ""
                      );
                      setValidationError("");
                    }}
                  >
                    <option value="">
                      Keep {getStatusLabel(task.status)}
                    </option>

                    {availableStatuses.map((status) => (
                      <option
                        key={status}
                        value={status}
                      >
                        {getStatusLabel(status)}
                      </option>
                    ))}
                  </select>
                </div>
              )}

              {validationError && (
                <div className={styles.validationError}>
                  {validationError}
                </div>
              )}
            </>
          ) : (
            <p>
              {task.text}
            </p>
          )}

          {completed && task.finishedAt && (
            <div className={styles.finishedAt}>
              Finished: {new Date(task.finishedAt).toLocaleString()}
            </div>
          )}
        </div>

      </div>

      <div className={styles.actions}>

        {editing ? (
          <>
            <button
              className={styles.cancelEditButton}
              onClick={() => {
                setEditing(false);
                setValidationError("");
                setNextStatus("");
              }}
            >
              Cancel
            </button>

            <button
              className={styles.confirmButton}
              onClick={() => void handleConfirmEdit()}
            >
              Confirm
            </button>
          </>
        ) : (
          <button
            className={styles.editButton}
            onClick={beginEditing}
          >
            Edit
          </button>
        )}

        {confirmDelete ? (
          <>
            <button
              className={styles.cancelDeleteButton}
              onClick={() =>
                setConfirmDelete(false)
              }
            >
              Cancel
            </button>

            <button
              className={styles.deleteButton}
              onClick={handleDelete}
            >
              Confirm delete
            </button>
          </>
        ) : (
          <button
            className={styles.deleteButton}
            onClick={() =>
              setConfirmDelete(true)
            }
          >
            Delete
          </button>
        )}

      </div>

    </div>
  );
}
