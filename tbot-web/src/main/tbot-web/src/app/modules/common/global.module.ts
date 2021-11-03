import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {AppNavComponent} from './components/app-nav/app-nav.component';
import {PageNotFoundComponent} from './components/page-not-found/page-not-found.component';
import {MatToolbarModule} from '@angular/material/toolbar';
import {MatIconModule} from '@angular/material/icon';
import {FormInputComponent} from './components/form-input/form-input.component';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatSelectModule} from '@angular/material/select';
import {MatSlideToggleModule} from '@angular/material/slide-toggle';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {MatInputModule} from '@angular/material/input';

@NgModule({
  declarations: [AppNavComponent, PageNotFoundComponent, FormInputComponent],
  exports: [
    AppNavComponent,
    FormInputComponent
  ],
  imports: [
    CommonModule,
    MatToolbarModule,
    MatIconModule,
    MatFormFieldModule,
    MatSelectModule,
    MatSlideToggleModule,
    FormsModule,
    MatInputModule,
    ReactiveFormsModule,
  ],
  schemas: []
})
export class GlobalModule {
}
