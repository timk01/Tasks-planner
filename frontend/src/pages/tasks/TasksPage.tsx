import { useEffect, useState } from "react";

import Header from "../../components/Header";
import TaskCard from "../../components/TaskCard";
import TaskForm from "../../components/TaskForm";

import {
  getTasks,
  createTask,
  updateTask,
  deleteTask,
} from "../../api/taskApi";

import type {
  CreateTaskRequest,
  Task,
  UpdateTaskRequest,
} from "../../api/taskApi";

import styles from "./TasksPage.module.css";

type Filter =
  | "all"
  | "active"
  | "completed";

export default function TasksPage() {
  const [tasks, setTasks] =
    useState<Task[]>([]);

  const [loading, setLoading] =
    useState(true);

  const [error, setError] =
    useState("");

  const [showForm, setShowForm] =
    useState(false);

  const [filter, setFilter] =
    useState<Filter>("all");

  const [search, setSearch] =
    useState("");

  useEffect(() => {
    void loadTasks();
  }, []);

  async function loadTasks() {
    try {
      setError("");
      const data = await getTasks();
      setTasks(data);
    } catch (err) {
      setError(
        err instanceof Error
          ? err.message
          : "Failed to load tasks"
      );
    } finally {
      setLoading(false);
    }
  }

  async function handleCreateTask(
    data: CreateTaskRequest
  ) {
    try {
      setError("");
      const task = await createTask(data);

      setTasks((prev) => [
        ...prev,
        task,
      ]);

      setShowForm(false);
    } catch (err) {
      setError(
        err instanceof Error
          ? err.message
          : "Failed to create task"
      );
    }
  }

  async function handleUpdateTask(
    id: number,
    data: UpdateTaskRequest
  ) {
    setError("");

    const updated = await updateTask(
      id,
      data
    );

    replaceTask(updated);
  }

  async function handleDeleteTask(
    id: number
  ) {
    try {
      setError("");
      await deleteTask(id);

      setTasks((prev) =>
        prev.filter(
          (task) =>
            task.taskId !== id
        )
      );
    } catch (err) {
      setError(
        err instanceof Error
          ? err.message
          : "Failed to delete task"
      );
    }
  }

  function replaceTask(updated: Task) {
    setTasks((prev) =>
      prev.map((item) =>
        item.taskId === updated.taskId
          ? updated
          : item
      )
    );
  }

  const normalizedSearch =
    search.toLowerCase();

  const visibleTasks =
    tasks.filter((task) => {
      const matchesFilter =
        filter === "all" ||
        (
          filter === "active" &&
          task.status !== "FINISHED"
        ) ||
        (
          filter === "completed" &&
          task.status === "FINISHED"
        );

      const matchesSearch =
        task.header
          .toLowerCase()
          .includes(normalizedSearch) ||
        task.text
          .toLowerCase()
          .includes(normalizedSearch);

      return (
        matchesFilter &&
        matchesSearch
      );
    });

  const completedCount =
    tasks.filter(
      (task) =>
        task.status === "FINISHED"
    ).length;

  return (
    <>
      <Header />

      <main className={styles.page}>
        <div className={styles.container}>
          <div className={styles.top}>
            <div>
              <h1>
                My Tasks
              </h1>

              <p>
                {tasks.length} total ·{" "}
                {completedCount} completed
              </p>
            </div>

            <button
              className={styles.newButton}
              onClick={() =>
                setShowForm(true)
              }
            >
              + New task
            </button>
          </div>

          {error && (
            <div className={styles.error}>
              {error}
            </div>
          )}

          <div className={styles.searchWrapper}>
            <span className={styles.searchIcon}>
              🔍
            </span>

            <input
              className={styles.search}
              placeholder="Search tasks..."
              value={search}
              onChange={(event) =>
                setSearch(event.target.value)
              }
            />
          </div>

          <div className={styles.filters}>
            {(
              [
                "all",
                "active",
                "completed",
              ] as Filter[]
            ).map((item) => (
              <button
                key={item}
                className={
                  filter === item
                    ? styles.activeFilter
                    : ""
                }
                onClick={() =>
                  setFilter(item)
                }
              >
                {item}
              </button>
            ))}
          </div>

          {showForm && (
            <TaskForm
              onSubmit={handleCreateTask}
              onCancel={() =>
                setShowForm(false)
              }
            />
          )}

          {loading ? (
            <div>
              Loading...
            </div>
          ) : visibleTasks.length === 0 ? (
            <div className={styles.empty}>
              <h2>No tasks found</h2>
              <p>
                Create a task or change the current filter.
              </p>
            </div>
          ) : (
            <div className={styles.list}>
              {visibleTasks.map(
                (task) => (
                  <TaskCard
                    key={task.taskId}
                    task={task}
                    onUpdate={handleUpdateTask}
                    onDelete={handleDeleteTask}
                  />
                )
              )}
            </div>
          )}
        </div>
      </main>
    </>
  );
}
