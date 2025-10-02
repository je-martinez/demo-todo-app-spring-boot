import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NgIconComponent, provideIcons } from '@ng-icons/core';
import { heroClipboardDocumentList, heroPlus } from '@ng-icons/heroicons/outline';

@Component({
  selector: 'app-task-list-view',
  standalone: true,
  imports: [CommonModule, NgIconComponent],
  providers: [provideIcons({ heroClipboardDocumentList, heroPlus })],
  templateUrl: './task-list-view.html',
})
export class TaskListViewComponent {}
