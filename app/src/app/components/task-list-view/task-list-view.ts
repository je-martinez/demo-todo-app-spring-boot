import { Component, input, output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NgIconComponent, provideIcons } from '@ng-icons/core';
import { heroClipboardDocumentList } from '@ng-icons/heroicons/outline';
import { Task } from '@app/types';
import { TaskItem } from '../task-item/task-item';

@Component({
  selector: 'app-task-list-view',
  standalone: true,
  imports: [CommonModule, NgIconComponent, TaskItem],
  providers: [provideIcons({ heroClipboardDocumentList })],
  templateUrl: './task-list-view.html',
})
export class TaskListView {
  tasks = input.required<Task[]>();
  isLoading = input(false);
  onEditTask = output<Task>();

  handleEditTask(task: Task): void {
    this.onEditTask.emit(task);
  }
}
