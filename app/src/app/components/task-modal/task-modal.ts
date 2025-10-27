import { Component, effect, input, output } from '@angular/core';
import { CommonModule } from '@angular/common';
import {
  FormsModule,
  ReactiveFormsModule,
  FormBuilder,
  FormGroup,
  Validators,
} from '@angular/forms';
import { NgIconComponent, provideIcons } from '@ng-icons/core';
import { heroXMark, heroCheck } from '@ng-icons/heroicons/outline';
import { Task } from '@app/types';
import { format, formatISO } from 'date-fns';

@Component({
  selector: 'app-task-modal',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, NgIconComponent],
  providers: [
    provideIcons({
      heroXMark,
      heroCheck,
    }),
  ],
  templateUrl: './task-modal.html',
})
export class TaskModal {
  private fb = new FormBuilder();

  task = input<Task | null>(null);
  isOpen = input<boolean>(false);
  onClose = output<void>();
  onSave = output<{ title: string; description: string; date: string }>();

  taskForm: FormGroup;

  constructor() {
    const tomorrow = new Date();
    tomorrow.setDate(tomorrow.getDate() + 1);
    tomorrow.setHours(12, 0, 0, 0); // Set to noon
    const defaultDateTime = this.formatDateTimeForInput(tomorrow);

    this.taskForm = this.fb.group({
      title: ['', [Validators.required, Validators.minLength(3)]],
      description: ['', [Validators.required, Validators.minLength(5)]],
      date: [defaultDateTime, [Validators.required]],
    });

    // React to task input changes
    effect(() => {
      const task = this.task();
      if (task) {
        const taskDateTime = this.formatDateTimeForInput(new Date(task.date));
        this.taskForm.patchValue({
          title: task.title,
          description: task.description,
          date: taskDateTime,
        });
      } else if (!this.isOpen()) {
        this.taskForm.reset();
      }
    });
  }

  private formatDateTimeForInput(date: Date): string {
    // Format: YYYY-MM-DDTHH:mm for datetime-local input
    return format(date, "yyyy-MM-dd'T'HH:mm");
  }

  get title() {
    return this.taskForm.get('title');
  }

  get description() {
    return this.taskForm.get('description');
  }

  get date() {
    return this.taskForm.get('date');
  }

  handleClose(): void {
    this.taskForm.reset();
    this.onClose.emit();
  }

  handleSave(): void {
    if (this.taskForm.valid) {
      // Convert datetime-local value to ISO format
      const isoDateTime = this.convertToISOFormat(this.date!.value);

      this.onSave.emit({
        title: this.title!.value,
        description: this.description!.value,
        date: isoDateTime,
      });
      this.taskForm.reset();
      this.onClose.emit();
    }
  }

  private convertToISOFormat(datetimeLocal: string): string {
    // Convert YYYY-MM-DDTHH:mm to YYYY-MM-DDTHH:mm:ssZ (without milliseconds)
    const date = new Date(datetimeLocal);
    // Format as ISO string and remove milliseconds
    return formatISO(date, { representation: 'complete' }).replace(/\.\d{3}Z$/, 'Z');
  }

  get isEditMode(): boolean {
    return this.task() !== null;
  }
}
