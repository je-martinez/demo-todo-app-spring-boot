import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NgIconComponent, provideIcons } from '@ng-icons/core';
import { heroBars3, heroCalendarDays, heroPlus } from '@ng-icons/heroicons/outline';
import { TaskListView, CalendarView, TaskModal } from '../../components';
import { TasksFacade } from '@app/store';
import { Task } from '@app/types';

export type ViewMode = 'list' | 'calendar';

@Component({
  selector: 'app-your-tasks',
  standalone: true,
  imports: [CommonModule, NgIconComponent, TaskListView, CalendarView, TaskModal],
  providers: [provideIcons({ heroBars3, heroCalendarDays, heroPlus })],
  templateUrl: './your-tasks.html',
})
export class YourTasks implements OnInit {
  private readonly tasksFacade = inject(TasksFacade);
  currentView: ViewMode = 'list';
  tasks = this.tasksFacade.tasks;
  completedCount = this.tasksFacade.completedTasksCount;
  isLoadingAndEmptyTasks = this.tasksFacade.isLoadingAndEmptyTasks;

  // Modal state
  isModalOpen = signal(false);
  selectedTask = signal<Task | null>(null);

  ngOnInit(): void {
    this.loadTasks();
  }

  loadTasks(): void {
    this.tasksFacade.loadTasks();
  }

  setViewMode(mode: ViewMode): void {
    this.currentView = mode;
  }

  openCreateModal(): void {
    this.selectedTask.set(null);
    this.isModalOpen.set(true);
  }

  openEditModal(task: Task): void {
    this.selectedTask.set(task);
    this.isModalOpen.set(true);
  }

  closeModal(): void {
    this.isModalOpen.set(false);
    this.selectedTask.set(null);
  }

  handleTaskSave(data: { title: string; description: string; date: string }): void {
    const task = this.selectedTask();

    if (task) {
      // Update existing task
      this.tasksFacade.updateTask(task.id, data.title, data.description, data.date);
    } else {
      // Create new task
      this.tasksFacade.createTask(data.title, data.description, data.date);
    }

    this.loadTasks();
  }
}
