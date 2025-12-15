import { Routes } from '@angular/router';
import { LoginComponent } from './components/login/login.component';
import { HomeComponent } from './components/home/home.component';
import { ListaComponent as SegurosListaComponent } from './components/seguros/lista/lista.component';
import { FormComponent as SegurosFormComponent } from './components/seguros/form/form.component';
import { ListaComponent as ClientesListaComponent } from './components/clientes/lista/lista.component';
import { FormComponent as ClientesFormComponent } from './components/clientes/form/form.component';
import { ListaComponent as FuncionariosListaComponent } from './components/funcionarios/lista/lista.component';
import { FormComponent as FuncionariosFormComponent } from './components/funcionarios/form/form.component';
import { CotacaoComponent } from './components/cotacao/cotacao.component';
import { ListaComponent as ApolicesListaComponent } from './components/apolice/lista/lista.component';
import { FormComponent as ApolicesFormComponent } from './components/apolice/form/form.component';
import { DashboardComponent } from './components/dashboard/dashboard.component';
import { AuthGuard } from './guards/auth.guard';
import { FuncionarioGuard } from './guards/funcionario.guard';

export const routes: Routes = [
    { path: '', redirectTo: '/login', pathMatch: 'full' },
    { path: 'login', component: LoginComponent },
    { path: 'home', component: HomeComponent },

    // Seguros - Todos podem ver
    { path: 'seguros', component: SegurosListaComponent },
    { path: 'seguros/novo', component: SegurosFormComponent, canActivate: [FuncionarioGuard] },
    { path: 'seguros/editar/:id', component: SegurosFormComponent, canActivate: [FuncionarioGuard] },

    // Clientes - Apenas funcionários
    { path: 'clientes', component: ClientesListaComponent, canActivate: [FuncionarioGuard] },
    { path: 'clientes/novo', component: ClientesFormComponent, canActivate: [FuncionarioGuard] },
    { path: 'clientes/editar/:id', component: ClientesFormComponent, canActivate: [FuncionarioGuard] },

    // Funcionários - Apenas funcionários
    { path: 'funcionarios', component: FuncionariosListaComponent, canActivate: [FuncionarioGuard] },
    { path: 'funcionarios/novo', component: FuncionariosFormComponent, canActivate: [FuncionarioGuard] },
    { path: 'funcionarios/editar/:id', component: FuncionariosFormComponent, canActivate: [FuncionarioGuard] },

    // Cotação - Apenas funcionários
    { path: 'cotacao', component: CotacaoComponent, canActivate: [FuncionarioGuard] },

    // Apólices - Apenas funcionários
    { path: 'apolices', component: ApolicesListaComponent, canActivate: [FuncionarioGuard] },
    { path: 'apolices/novo', component: ApolicesFormComponent, canActivate: [FuncionarioGuard] },

    // Dashboard - Apenas funcionários
    { path: 'dashboard', component: DashboardComponent, canActivate: [FuncionarioGuard] },

    { path: '**', redirectTo: '/home' }
];
