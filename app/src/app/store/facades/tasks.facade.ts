import { Injectable, inject, computed } from '@angular/core';
import { TasksStore } from '../tasks.store';
import { Task } from '@app/types';

@Injectable({
  providedIn: 'root',
})
export class TasksFacade {
  private readonly tasksStore = inject(TasksStore);

  // State selectors - reactive state access
  readonly tasks = this.tasksStore.tasks;
  readonly selectedTask = this.tasksStore.selectedTask;
  readonly isLoading = this.tasksStore.isLoading;
  readonly error = this.tasksStore.error;

  // Computed selectors - derived state
  readonly hasTasks = this.tasksStore.hasTasks;
  readonly hasError = this.tasksStore.hasError;
  readonly isLoadingTasks = this.tasksStore.isLoadingTasks;

  // Computed helpers for common use cases
  readonly completedTasks = computed(() => this.tasks().filter(task => task.completed));

  readonly uncompletedTasks = computed(() => this.tasks().filter(task => !task.completed));

  readonly tasksCount = computed(() => this.tasks().length);

  readonly completedTasksCount = computed(() => this.completedTasks().length);

  readonly completionPercentage = computed(() => {
    const total = this.tasksCount();
    if (total === 0) return 0;
    return Math.round((this.completedTasksCount() / total) * 100);
  });

  // Action methods - clean API for store actions
  loadTasks() {
    this.tasksStore.loadTasks();
  }

  loadTask(id: string) {
    this.tasksStore.loadTask(id);
  }

  createTask(title: string, description: string, cover: string) {
    this.tasksStore.createTask({ title, description, cover });
  }

  updateTask(id: string, title: string, description: string, cover: string) {
    this.tasksStore.updateTask({ id, title, description, cover });
  }

  deleteTask(id: string) {
    this.tasksStore.deleteTask(id);
  }

  markAsCompleted(id: string) {
    this.tasksStore.markAsCompleted(id);
  }

  markAsUncompleted(id: string) {
    this.tasksStore.markAsUncompleted(id);
  }

  // Selection methods
  selectTask(task: Task | null) {
    this.tasksStore.selectTask(task);
  }

  clearSelectedTask() {
    this.tasksStore.clearSelectedTask();
  }

  // Error handling
  clearError() {
    this.tasksStore.clearError();
  }

  resetTasks() {
    this.tasksStore.resetTasks();
  }

  // Helper methods for common patterns
  hasTaskWithId(id: string): boolean {
    return this.tasks().some(task => task.id === id);
  }

  getTaskById(id: string): Task | undefined {
    return this.tasks().find(task => task.id === id);
  }

  getTasksByDateRange(startDate: Date, endDate: Date): Task[] {
    return this.tasks().filter(task => {
      const taskDate = new Date(task.date);
      return taskDate >= startDate && taskDate <= endDate;
    });
  }

  getTasksByTitle(searchTerm: string): Task[] {
    const lowerSearchTerm = searchTerm.toLowerCase();
    return this.tasks().filter(task => task.title.toLowerCase().includes(lowerSearchTerm));
  }

  // Error handling helpers
  getErrorMessage(): string | null {
    return this.error()?.message || null;
  }

  getErrorList(): string[] {
    return this.error()?.errors || [];
  }

  // State snapshot helpers (for non-reactive access)
  getTasksStateSnapshot() {
    return {
      tasks: this.tasks(),
      selectedTask: this.selectedTask(),
      isLoading: this.isLoading(),
      error: this.error(),
      hasTasks: this.hasTasks(),
      hasError: this.hasError(),
      errorMessage: this.getErrorMessage(),
      errorList: this.getErrorList(),
      tasksCount: this.tasksCount(),
      completedTasksCount: this.completedTasksCount(),
      completionPercentage: this.completionPercentage(),
    };
  }
}
