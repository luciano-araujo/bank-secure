import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Injectable({
    providedIn: 'root'
})
export class FuncionarioGuard implements CanActivate {
    constructor(
        private authService: AuthService,
        private router: Router
    ) { }

    canActivate(): boolean {
        if (this.authService.isAuthenticated() && this.authService.isFuncionario()) {
            return true;
        }
        this.router.navigate(['/home']);
        return false;
    }
}
