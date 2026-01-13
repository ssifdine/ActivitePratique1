import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NotAuthorizedComponent } from './not-authorized.component';

describe('NotAuthorized', () => {
  let component: NotAuthorizedComponent;
  let fixture: ComponentFixture<NotAuthorizedComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NotAuthorizedComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(NotAuthorizedComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
