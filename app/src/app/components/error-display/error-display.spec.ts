import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ErrorDisplayComponent } from './error-display';
import { ApiError } from '@app/store/auth.store';

describe('ErrorDisplayComponent', () => {
  let component: ErrorDisplayComponent;
  let fixture: ComponentFixture<ErrorDisplayComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ErrorDisplayComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(ErrorDisplayComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should not display when error is null', () => {
    component.error = null;
    fixture.detectChanges();

    const errorElement = fixture.nativeElement.querySelector('.bg-red-50');
    expect(errorElement).toBeNull();
  });

  it('should display error message when error is provided', () => {
    const mockError: ApiError = {
      message: 'Validation failed',
      errors: ['Password must be at least 9 characters long'],
    };

    component.error = mockError;
    fixture.detectChanges();

    const errorElement = fixture.nativeElement.querySelector('.bg-red-50');
    expect(errorElement).toBeTruthy();

    const messageElement = fixture.nativeElement.querySelector('.text-red-800');
    expect(messageElement.textContent.trim()).toBe('Validation failed');
  });

  it('should display error list when errors array is provided', () => {
    const mockError: ApiError = {
      message: 'Validation failed',
      errors: [
        'Password must be at least 9 characters long',
        'Password must contain uppercase letter',
      ],
    };

    component.error = mockError;
    fixture.detectChanges();

    const errorItems = fixture.nativeElement.querySelectorAll('li');
    expect(errorItems.length).toBe(2);
    expect(errorItems[0].textContent.trim()).toContain(
      'Password must be at least 9 characters long'
    );
    expect(errorItems[1].textContent.trim()).toContain('Password must contain uppercase letter');
  });

  it('should emit clearError when close button is clicked', () => {
    const mockError: ApiError = {
      message: 'Test error',
      errors: [],
    };

    component.error = mockError;
    component.showCloseButton = true;
    fixture.detectChanges();

    spyOn(component.clearError, 'emit');

    const closeButton = fixture.nativeElement.querySelector('button');
    closeButton.click();

    expect(component.clearError.emit).toHaveBeenCalled();
  });

  it('should not show close button when showCloseButton is false', () => {
    const mockError: ApiError = {
      message: 'Test error',
      errors: [],
    };

    component.error = mockError;
    component.showCloseButton = false;
    fixture.detectChanges();

    const closeButton = fixture.nativeElement.querySelector('button');
    expect(closeButton).toBeNull();
  });
});
