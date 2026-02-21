import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { provideRouter } from '@angular/router';
import { HomeComponent } from './home.component';

describe('HomeComponent', () => {
  let component: HomeComponent;
  let fixture: ComponentFixture<HomeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HomeComponent],
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        provideRouter([])
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(HomeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should have empty search query', () => {
    expect(component.searchQuery).toBe('');
  });

  it('should have empty suggestions', () => {
    expect(component.suggestions).toEqual([]);
  });

  it('should render hero section', () => {
    const compiled = fixture.nativeElement;
    expect(compiled.querySelector('.hero-title')).toBeTruthy();
    expect(compiled.querySelector('.hero-title').textContent).toContain('Bewerte deine Nachbarschaft');
  });

  it('should render steps section', () => {
    const compiled = fixture.nativeElement;
    const steps = compiled.querySelectorAll('.step');
    expect(steps.length).toBe(3);
  });
});
