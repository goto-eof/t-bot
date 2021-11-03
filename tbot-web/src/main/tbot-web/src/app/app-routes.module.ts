import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {PageNotFoundComponent} from './modules/common/components/page-not-found/page-not-found.component';

const routes: Routes = [
  {
    path: 'cp',
    loadChildren: () => import('./modules/control-panel/control-panel.module').then(m => m.ControlPanelModule)
  },
  {
    path: '**',
    component: PageNotFoundComponent
  },
];

@NgModule({
  imports: [
    RouterModule.forRoot(
      routes,
      {
        enableTracing: true,
        /*useHash: true*/
      }
    )
  ],
  exports: [
    RouterModule
  ],
  providers: []
})
export class AppRoutesModule {
}
