import { Component, inject, input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NgIconComponent, provideIcons } from '@ng-icons/core';
import { heroCheckCircle, heroXCircle, heroCalendar, heroTrash } from '@ng-icons/heroicons/outline';
import { NgxBlurhashComponent } from 'ngx-blurhash-render';
import { TasksFacade } from '../../store';
import { Task } from '@app/types';

@Component({
  selector: 'app-task-item',
  standalone: true,
  imports: [CommonModule, NgIconComponent, NgxBlurhashComponent],
  providers: [
    provideIcons({
      heroCheckCircle,
      heroXCircle,
      heroCalendar,
      heroTrash,
    }),
  ],
  templateUrl: './task-item.html',
})
export class TaskItem {
  private readonly tasksFacade = inject(TasksFacade);

  task = input.required<Task>();

  toggleCompletion(): void {
    const task = this.task();
    if (task.completed) {
      this.tasksFacade.markAsUncompleted(task.id);
    } else {
      this.tasksFacade.markAsCompleted(task.id);
    }
  }

  deleteTask(): void {
    const task = this.task();
    if (confirm(`Are you sure you want to delete "${task.title}"?`)) {
      this.tasksFacade.deleteTask(task.id);
    }
  }

  formatDate(date: Date | string): string {
    return new Date(date).toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
    });
  }
}
