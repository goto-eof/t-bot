import {EventEmitter, Injectable, Output} from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class CommonService {

  @Output()
  public commonServiceEmitter: EventEmitter<any> = new EventEmitter();

  constructor() {}
}
