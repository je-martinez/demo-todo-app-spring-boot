import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NgIconComponent, provideIcons } from '@ng-icons/core';
import { heroCalendarDays, heroClock } from '@ng-icons/heroicons/outline';

@Component({
  selector: 'app-calendar-view',
  standalone: true,
  imports: [CommonModule, NgIconComponent],
  providers: [provideIcons({ heroCalendarDays, heroClock })],
  templateUrl: './calendar-view.html',
})
export class CalendarView {}
