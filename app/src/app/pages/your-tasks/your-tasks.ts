import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NgIconComponent, provideIcons } from '@ng-icons/core';
import { heroBars3, heroCalendarDays } from '@ng-icons/heroicons/outline';
import { TaskListViewComponent, CalendarViewComponent } from '../../components';

export type ViewMode = 'list' | 'calendar';

@Component({
  selector: 'app-your-tasks',
  standalone: true,
  imports: [CommonModule, NgIconComponent, TaskListViewComponent, CalendarViewComponent],
  providers: [provideIcons({ heroBars3, heroCalendarDays })],
  templateUrl: './your-tasks.html',
})
export class YourTasks {
  currentView: ViewMode = 'list';

  setViewMode(mode: ViewMode): void {
    this.currentView = mode;
  }
}
