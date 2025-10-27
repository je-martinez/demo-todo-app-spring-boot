import { computed, inject } from '@angular/core';
import { patchState, signalStore, withComputed, withMethods, withState } from '@ngrx/signals';
import { rxMethod } from '@ngrx/signals/rxjs-interop';
import { catchError, EMPTY, map, of, switchMap, tap } from 'rxjs';
import { TasksApi } from '@app/api/tasks-api';
import { Task, ApiError } from '@app/types';

export interface TasksState {
  tasks: Task[];
  selectedTask: Task | null;
  isLoading: boolean;
  error: ApiError | null;
}

const initialState: TasksState = {
  tasks: [],
  selectedTask: null,
  isLoading: false,
  error: null,
};

export const TasksStore = signalStore(
  { providedIn: 'root' },
  withState(initialState),
  withComputed(store => ({
    hasTasks: computed(() => store.tasks().length > 0),
    hasError: computed(() => !!store.error()),
    isLoadingTasks: computed(() => store.isLoading()),
  })),
  withMethods((store, tasksApi = inject(TasksApi)) => ({
    // Get all tasks
    loadTasks: rxMethod<void>(c$ =>
      c$.pipe(
        tap(() => patchState(store, { isLoading: true, error: null })),
        switchMap(() =>
          tasksApi.getTasks().pipe(
            map(response => response),
            tap(tasks => {
              patchState(store, { tasks, isLoading: false });
            }),
            catchError((error: ApiError) => {
              console.error('Error loading tasks:', error);
              patchState(store, { error, isLoading: false });
              return EMPTY;
            })
          )
        )
      )
    ),

    // Get a single task
    loadTask: rxMethod<string>(c$ =>
      c$.pipe(
        tap(() => patchState(store, { isLoading: true, error: null })),
        switchMap(id =>
          tasksApi.getTask(id).pipe(
            map(response => response),
            tap(task => {
              patchState(store, { selectedTask: task, isLoading: false });
              // Also update the task in the tasks array if it exists
              const tasks = store.tasks();
              const index = tasks.findIndex(t => t.id === id);
              if (index !== -1) {
                const updatedTasks = [...tasks];
                updatedTasks[index] = task;
                patchState(store, { tasks: updatedTasks });
              }
            }),
            catchError((error: ApiError) => {
              console.error('Error loading task:', error);
              patchState(store, { error, isLoading: false });
              return EMPTY;
            })
          )
        )
      )
    ),

    // Create a task
    createTask: rxMethod<{ title: string; description: string; date: string }>(c$ =>
      c$.pipe(
        tap(() => patchState(store, { isLoading: true, error: null })),
        switchMap(data =>
          tasksApi.createTask(data.title, data.description, data.date).pipe(
            map(response => response),
            tap(newTask => {
              const currentTasks = store.tasks();
              patchState(store, {
                tasks: [newTask, ...currentTasks],
                isLoading: false,
              });
            }),
            catchError((error: ApiError) => {
              console.error('Error creating task:', error);
              patchState(store, { error, isLoading: false });
              return EMPTY;
            })
          )
        )
      )
    ),

    // Update a task
    updateTask: rxMethod<{ id: string; title: string; description: string; date: string }>(c$ =>
      c$.pipe(
        tap(() => patchState(store, { isLoading: true, error: null })),
        switchMap(data =>
          tasksApi.updateTask(data.id, data.title, data.description, data.date).pipe(
            map(response => response),
            tap(updatedTask => {
              const tasks = store.tasks();
              const index = tasks.findIndex(t => t.id === data.id);
              if (index !== -1) {
                const updatedTasks = [...tasks];
                updatedTasks[index] = updatedTask;
                patchState(store, { tasks: updatedTasks, isLoading: false });
              }
              // Update selected task if it's the one being updated
              if (store.selectedTask()?.id === data.id) {
                patchState(store, { selectedTask: updatedTask });
              }
            }),
            catchError((error: ApiError) => {
              console.error('Error updating task:', error);
              patchState(store, { error, isLoading: false });
              return EMPTY;
            })
          )
        )
      )
    ),

    // Delete a task
    deleteTask: rxMethod<string>(c$ =>
      c$.pipe(
        tap(() => patchState(store, { isLoading: true, error: null })),
        switchMap(id =>
          tasksApi.deleteTask(id).pipe(
            tap(() => {
              const tasks = store.tasks();
              const filteredTasks = tasks.filter(t => t.id !== id);
              patchState(store, {
                tasks: filteredTasks,
                isLoading: false,
              });
              // Clear selected task if it was the one deleted
              if (store.selectedTask()?.id === id) {
                patchState(store, { selectedTask: null });
              }
            }),
            catchError((error: ApiError) => {
              console.error('Error deleting task:', error);
              patchState(store, { error, isLoading: false });
              return EMPTY;
            })
          )
        )
      )
    ),

    // Mark task as completed
    markAsCompleted: rxMethod<string>(c$ =>
      c$.pipe(
        tap(() => patchState(store, { isLoading: true, error: null })),
        switchMap(id =>
          tasksApi.markAsCompleted(id).pipe(
            map(response => response),
            tap(updatedTask => {
              const tasks = store.tasks();
              const index = tasks.findIndex(t => t.id === id);
              if (index !== -1) {
                const updatedTasks = [...tasks];
                updatedTasks[index] = updatedTask;
                patchState(store, { tasks: updatedTasks, isLoading: false });
              }
              // Update selected task if it's the one being updated
              if (store.selectedTask()?.id === id) {
                patchState(store, { selectedTask: updatedTask });
              }
            }),
            catchError((error: ApiError) => {
              console.error('Error marking task as completed:', error);
              patchState(store, { error, isLoading: false });
              return EMPTY;
            })
          )
        )
      )
    ),

    // Mark task as uncompleted
    markAsUncompleted: rxMethod<string>(c$ =>
      c$.pipe(
        tap(() => patchState(store, { isLoading: true, error: null })),
        switchMap(id =>
          tasksApi.markAsUncompleted(id).pipe(
            map(response => response),
            tap(updatedTask => {
              const tasks = store.tasks();
              const index = tasks.findIndex(t => t.id === id);
              if (index !== -1) {
                const updatedTasks = [...tasks];
                updatedTasks[index] = updatedTask;
                patchState(store, { tasks: updatedTasks, isLoading: false });
              }
              // Update selected task if it's the one being updated
              if (store.selectedTask()?.id === id) {
                patchState(store, { selectedTask: updatedTask });
              }
            }),
            catchError((error: ApiError) => {
              console.error('Error marking task as uncompleted:', error);
              patchState(store, { error, isLoading: false });
              return EMPTY;
            })
          )
        )
      )
    ),
  })),
  withMethods(store => ({
    // Clear selected task
    clearSelectedTask: () => {
      patchState(store, { selectedTask: null });
    },

    // Select a task
    selectTask: (task: Task | null) => {
      patchState(store, { selectedTask: task });
    },

    // Clear error
    clearError: () => {
      patchState(store, { error: null });
    },

    // Reset store
    resetTasks: () => {
      patchState(store, initialState);
    },
  }))
);
