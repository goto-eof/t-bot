import {Component, OnInit} from '@angular/core';
import {environment} from '../../../../../environments/environment';

@Component({
  selector: 'app-nav',
  templateUrl: './app-nav.component.html',
  styleUrls: ['./app-nav.component.css']
})
export class AppNavComponent implements OnInit {

  env: string;

  constructor() {
  }

  ngOnInit(): void {
    this.env = environment.production === true ? 'prod' : 'test';
  }

}
