import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import {Navbar} from './layout/navbar/navbar';
import {Login} from './pages/auth/login/login';

@Component({
  selector: 'app-root',
  imports: [
    RouterOutlet,
  ],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  protected title = 'microservices-client';
}
