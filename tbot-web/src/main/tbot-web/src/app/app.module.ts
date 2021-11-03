import {APP_INITIALIZER, NgModule} from '@angular/core';
import {AppComponent} from './app.component';
import {AppInitializerService} from './app-initializer.service';
import {GlobalModule} from './modules/common/global.module';
import {HttpClientModule} from '@angular/common/http';
import {AppRoutesModule} from './app-routes.module';
import {BrowserModule} from '@angular/platform-browser';
import {NoopAnimationsModule} from '@angular/platform-browser/animations';
import {MatSidenavModule} from '@angular/material/sidenav';

import {MatToolbarModule} from '@angular/material/toolbar';
import {MatListModule} from '@angular/material/list';
import {MatIconModule} from '@angular/material/icon';
import {YesNotDialogComponent} from './modules/control-panel/control-panel-home/dialogs/yes-not-dialog/yes-not-dialog.component';
import {MatDialogModule, MatDialogRef} from '@angular/material/dialog';


@NgModule({
  declarations: [
    AppComponent,
    YesNotDialogComponent
  ],
  imports: [
    AppRoutesModule,
    GlobalModule,
    HttpClientModule,
    BrowserModule,
    NoopAnimationsModule,
    MatSidenavModule,
    MatToolbarModule,
    MatListModule,
    MatIconModule,
    MatDialogModule
    //BrowserAnimationsModule,
  ],
  providers: [{
    provide: APP_INITIALIZER,
    useFactory: (appInit: AppInitializerService) => () => appInit.getConfiguration().toPromise(),
    deps: [AppInitializerService],
    multi: true
  },
    {
      provide: MatDialogRef,
      useValue: {}
    },
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
}
