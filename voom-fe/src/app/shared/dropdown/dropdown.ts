import { Component, Input, Output, EventEmitter } from '@angular/core';
import { MatSelectModule } from '@angular/material/select';
import { MatFormFieldModule } from '@angular/material/form-field';

@Component({
  selector: 'app-dropdown',
  templateUrl: 'dropdown.html',
  imports: [MatFormFieldModule, MatSelectModule],
})
export class Dropdown {
  @Input() options: { label: string; value: string | number }[] = [];
  @Input() selected: string | number | null = null;
  @Output() selectedChange = new EventEmitter<string | number>();
}
