import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {ControlPanelHomeComponent} from './control-panel-home/control-panel-home-component.component';


const routes: Routes = [
  {
    path: 'home',
    component: ControlPanelHomeComponent
  }
];

@NgModule({
  imports: [
    RouterModule.forChild(routes)
  ],
  exports: [
    RouterModule
  ],
  providers: []
})
export class ControlPanelRoutesModule {
}
