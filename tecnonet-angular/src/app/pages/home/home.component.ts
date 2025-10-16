import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PlanService } from '../../services/plan.service';
import { Plan } from '../../models/plan.model';

@Component({
  selector: 'app-home',
  imports: [CommonModule],
  templateUrl: './home.component.html'
})
export class HomeComponent implements OnInit {
  planes: Plan[] = [];

  constructor(private planService: PlanService) {}

  ngOnInit(): void {
    this.planService.getPlanes().subscribe(data => {
      this.planes = data;
    });
  }
}