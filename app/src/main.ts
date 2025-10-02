import { bootstrapApplication } from '@angular/platform-browser';
import { appConfig } from './app/app.config';
import { App } from './app/app';
import { AppFacade } from './app/store/facades';

bootstrapApplication(App, appConfig)
  .then(appRef => {
    // Initialize app after bootstrap
    const appFacade = appRef.injector.get(AppFacade);
    appFacade.initializeApp();
  })
  .catch(err => console.error(err));
