import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DashboardOperarioComponent } from './dashboard-operario.component';

describe('DashboardOperarioComponent', () => {
  let component: DashboardOperarioComponent;
  let fixture: ComponentFixture<DashboardOperarioComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DashboardOperarioComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DashboardOperarioComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
