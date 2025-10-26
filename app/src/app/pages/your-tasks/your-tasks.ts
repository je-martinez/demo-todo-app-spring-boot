import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NgIconComponent, provideIcons } from '@ng-icons/core';
import { heroBars3, heroCalendarDays, heroPlus } from '@ng-icons/heroicons/outline';
import { TaskListView, CalendarView } from '../../components';
import { TasksFacade } from '@app/store';

export type ViewMode = 'list' | 'calendar';

@Component({
  selector: 'app-your-tasks',
  standalone: true,
  imports: [CommonModule, NgIconComponent, TaskListView, CalendarView],
  providers: [provideIcons({ heroBars3, heroCalendarDays, heroPlus })],
  templateUrl: './your-tasks.html',
})
export class YourTasks implements OnInit {
  private readonly tasksFacade = inject(TasksFacade);
  currentView: ViewMode = 'list';
  tasks = this.tasksFacade.tasks;
  completedCount = this.tasksFacade.completedTasksCount;
  isLoading = this.tasksFacade.isLoading;

  ngOnInit(): void {
    this.loadTasks();
  }

  loadTasks(): void {
    this.tasksFacade.loadTasks();
  }

  setViewMode(mode: ViewMode): void {
    this.currentView = mode;
  }
}
