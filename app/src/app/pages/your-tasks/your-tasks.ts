import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NgIconComponent, provideIcons } from '@ng-icons/core';
import { heroBars3, heroCalendarDays, heroPlus } from '@ng-icons/heroicons/outline';
import { TaskListView, CalendarView } from '../../components';

export type ViewMode = 'list' | 'calendar';

@Component({
  selector: 'app-your-tasks',
  standalone: true,
  imports: [CommonModule, NgIconComponent, TaskListView, CalendarView],
  providers: [provideIcons({ heroBars3, heroCalendarDays, heroPlus })],
  templateUrl: './your-tasks.html',
})
export class YourTasks {
  currentView: ViewMode = 'list';

  setViewMode(mode: ViewMode): void {
    this.currentView = mode;
  }
}
