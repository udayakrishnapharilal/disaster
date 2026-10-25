/**
 * Disaster Management System - Authentication & Session Manager
 */

class AuthManager {
    constructor() {
        this.currentUser = this.loadUser();
    }

    loadUser() {
        const stored = localStorage.getItem('DMS_CURRENT_USER');
        if (stored) {
            try {
                return JSON.parse(stored);
            } catch (e) {
                console.error('Error parsing stored user', e);
            }
        }
        // Default demo user: Administrator
        return {
            id: 1,
            username: 'admin',
            fullName: 'Dr. Robert Vance',
            role: 'ADMIN',
            email: 'admin@disasterrelief.org'
        };
    }

    setUser(user, token = null) {
        this.currentUser = user;
        localStorage.setItem('DMS_CURRENT_USER', JSON.stringify(user));
        if (token) {
            localStorage.setItem('DMS_AUTH_TOKEN', token);
        }
        this.updateUi();
    }

    getUser() {
        return this.currentUser;
    }

    getRole() {
        return this.currentUser ? this.currentUser.role : 'ANONYMOUS';
    }

    isAdmin() {
        return this.getRole() === 'ADMIN';
    }

    isVictim() {
        return this.getRole() === 'VICTIM';
    }

    isVolunteer() {
        return this.getRole() === 'VOLUNTEER';
    }

    isRescueTeam() {
        return this.getRole() === 'RESCUE_TEAM';
    }

    switchDemoRole(role) {
        let demoUser;
        switch (role) {
            case 'ADMIN':
                demoUser = { id: 1, username: 'admin', fullName: 'Dr. Robert Vance', role: 'ADMIN', email: 'admin@disasterrelief.org' };
                break;
            case 'VICTIM':
                demoUser = { id: 2, username: 'victim1', fullName: 'Alice Johnson', role: 'VICTIM', email: 'victim1@email.com' };
                break;
            case 'VOLUNTEER':
                demoUser = { id: 3, username: 'volunteer1', fullName: 'Sarah Connor', role: 'VOLUNTEER', email: 'volunteer1@email.com' };
                break;
            case 'RESCUE_TEAM':
                demoUser = { id: 101, username: 'capt_wilson', fullName: 'Capt. James Wilson (NDRF)', role: 'RESCUE_TEAM', email: 'wilson@rescue.gov' };
                break;
            default:
                demoUser = { id: 1, username: 'admin', fullName: 'Dr. Robert Vance', role: 'ADMIN', email: 'admin@disasterrelief.org' };
        }
        this.setUser(demoUser, 'demo-token-' + role.toLowerCase());
        showToast('Switched Role', `Now operating as ${demoUser.fullName} (${demoUser.role})`, 'info');
        
        // Navigate to appropriate default view for this role
        if (role === 'ADMIN') navigateTo('admin-dashboard');
        else if (role === 'VICTIM') navigateTo('victim-dashboard');
        else if (role === 'VOLUNTEER') navigateTo('volunteer-dashboard');
        else if (role === 'RESCUE_TEAM') navigateTo('rescue-team-dashboard');
        else navigateTo('home');
    }

    logout() {
        localStorage.removeItem('DMS_CURRENT_USER');
        localStorage.removeItem('DMS_AUTH_TOKEN');
        this.currentUser = null;
        this.updateUi();
        navigateTo('login');
        showToast('Logged Out', 'You have been signed out successfully.', 'info');
    }

    updateUi() {
        const nameEl = document.getElementById('user-full-name');
        const roleEl = document.getElementById('user-role-badge');
        const userMenu = document.getElementById('user-dropdown');
        const loginBtn = document.getElementById('nav-login-btn');

        if (this.currentUser) {
            if (nameEl) nameEl.textContent = this.currentUser.fullName || this.currentUser.username;
            if (roleEl) {
                roleEl.textContent = this.currentUser.role;
                roleEl.className = `badge ${this.getRoleBadgeClass(this.currentUser.role)}`;
            }
            if (userMenu) userMenu.classList.remove('d-none');
            if (loginBtn) loginBtn.classList.add('d-none');
        } else {
            if (userMenu) userMenu.classList.add('d-none');
            if (loginBtn) loginBtn.classList.remove('d-none');
        }

        // Highlight active demo button
        document.querySelectorAll('.demo-role-btn').forEach(btn => {
            btn.classList.remove('active');
            if (this.currentUser && btn.dataset.role === this.currentUser.role) {
                btn.classList.add('active');
            }
        });

        // Filter sidebar links based on role
        this.filterNavByRole();
    }

    getRoleBadgeClass(role) {
        switch (role) {
            case 'ADMIN': return 'bg-danger';
            case 'RESCUE_TEAM': return 'bg-warning text-dark';
            case 'VOLUNTEER': return 'bg-success';
            case 'VICTIM': return 'bg-primary';
            default: return 'bg-secondary';
        }
    }

    filterNavByRole() {
        const role = this.getRole();
        document.querySelectorAll('[data-allow-roles]').forEach(el => {
            const allowed = el.dataset.allowRoles.split(',');
            if (allowed.includes('ALL') || allowed.includes(role)) {
                el.classList.remove('d-none');
            } else {
                el.classList.add('d-none');
            }
        });
    }
}

const auth = new AuthManager();
