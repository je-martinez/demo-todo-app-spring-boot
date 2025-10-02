import { ComponentFixture, TestBed } from '@angular/core/testing';
import { PulseLoader } from './pulse-loader';

describe('PulseLoader', () => {
  let component: PulseLoader;
  let fixture: ComponentFixture<PulseLoader>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PulseLoader],
    }).compileComponents();

    fixture = TestBed.createComponent(PulseLoader);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should have default values', () => {
    expect(component.size).toBe('md');
    expect(component.variant).toBe('dots');
    expect(component.color).toBe('primary');
    expect(component.showText).toBe(true);
    expect(component.text).toBe('Loading...');
    expect(component.overlay).toBe(true);
    expect(component.blur).toBe(false);
    expect(component.fullScreen).toBe(true);
  });

  it('should apply correct size classes', () => {
    component.size = 'sm';
    expect(component.sizeClasses).toBe('text-sm');

    component.size = 'md';
    expect(component.sizeClasses).toBe('text-base');

    component.size = 'lg';
    expect(component.sizeClasses).toBe('text-lg');

    component.size = 'xl';
    expect(component.sizeClasses).toBe('text-xl');
  });

  it('should apply correct color classes', () => {
    component.color = 'primary';
    expect(component.colorClasses).toBe('text-blue-600');

    component.color = 'secondary';
    expect(component.colorClasses).toBe('text-gray-600');

    component.color = 'accent';
    expect(component.colorClasses).toBe('text-purple-600');

    component.color = 'neutral';
    expect(component.colorClasses).toBe('text-gray-500');

    component.color = 'white';
    expect(component.colorClasses).toBe('text-white');
  });

  it('should apply correct pulse color classes', () => {
    component.color = 'primary';
    expect(component.pulseColorClasses).toBe('bg-blue-600');

    component.color = 'secondary';
    expect(component.pulseColorClasses).toBe('bg-gray-600');

    component.color = 'accent';
    expect(component.pulseColorClasses).toBe('bg-purple-600');

    component.color = 'neutral';
    expect(component.pulseColorClasses).toBe('bg-gray-500');

    component.color = 'white';
    expect(component.pulseColorClasses).toBe('bg-white');
  });

  it('should include full screen classes when fullScreen is true', () => {
    component.fullScreen = true;
    component.overlay = true;
    component.blur = false;

    const containerClasses = component.containerClasses;
    expect(containerClasses).toContain('fixed inset-0 z-50');
    expect(containerClasses).toContain('bg-white/80');
  });

  it('should include blur classes when blur is true', () => {
    component.fullScreen = true;
    component.overlay = true;
    component.blur = true;

    const containerClasses = component.containerClasses;
    expect(containerClasses).toContain('backdrop-blur-sm');
  });

  it('should not include full screen classes when fullScreen is false', () => {
    component.fullScreen = false;
    component.overlay = false;
    component.blur = false;

    const containerClasses = component.containerClasses;
    expect(containerClasses).not.toContain('fixed inset-0 z-50');
    expect(containerClasses).not.toContain('bg-white/80');
  });

  it('should render dots variant by default', () => {
    component.variant = 'dots';
    fixture.detectChanges();

    const dots = fixture.nativeElement.querySelectorAll('.w-3.h-3.rounded-full');
    expect(dots.length).toBe(3);
  });

  it('should render bars variant', () => {
    component.variant = 'bars';
    fixture.detectChanges();

    const bars = fixture.nativeElement.querySelectorAll('.w-1.bg-current');
    expect(bars.length).toBe(5);
  });

  it('should render circles variant', () => {
    component.variant = 'circles';
    fixture.detectChanges();

    const circles = fixture.nativeElement.querySelectorAll('.w-8.h-8.rounded-full');
    expect(circles.length).toBe(2);
  });

  it('should render squares variant', () => {
    component.variant = 'squares';
    fixture.detectChanges();

    const squares = fixture.nativeElement.querySelectorAll('.w-3.h-3');
    expect(squares.length).toBe(4);
  });

  it('should render waves variant', () => {
    component.variant = 'waves';
    fixture.detectChanges();

    const waves = fixture.nativeElement.querySelectorAll('.w-1.rounded-full');
    expect(waves.length).toBe(5);
  });

  it('should show text when showText is true', () => {
    component.showText = true;
    component.text = 'Custom loading text';
    fixture.detectChanges();

    const textElement = fixture.nativeElement.querySelector('.mt-4.font-medium');
    expect(textElement).toBeTruthy();
    expect(textElement.textContent.trim()).toBe('Custom loading text');
  });

  it('should not show text when showText is false', () => {
    component.showText = false;
    fixture.detectChanges();

    const textElement = fixture.nativeElement.querySelector('.mt-4.font-medium');
    expect(textElement).toBeFalsy();
  });
});
