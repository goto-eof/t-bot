import {AfterViewInit, Component, Input, OnInit} from '@angular/core';
import {DomandaDto} from './dto/domanda-dto';
import {environment} from '../../../../../environments/environment';
import {FormControl, FormGroup, Validators} from '@angular/forms';

@Component({
  selector: 'app-form-input',
  templateUrl: './form-input.component.html',
  styleUrls: ['./form-input.component.css']
})
export class FormInputComponent implements OnInit, AfterViewInit {

  @Input()
  domanda: DomandaDto;

  @Input()
  form: FormGroup;

  formControl: FormControl;

  readonly debug: boolean = environment.questionario.showCodiceDomanda;
  debugClass = 'font-weight-bold';

  constructor() {


  }

  ngOnInit(): void {
    this.formControl = new FormControl('', this.domanda.obbligatorio === true && this.domanda.formato !== 'BOL' ? Validators.required : null);
    this.form.addControl(this.domanda.id, this.formControl);
  }

  ngAfterViewInit(): void {
  }

  logga(valore: DomandaDto): void {
    console.log(valore);
  }

}
