/**
 * Disaster Management System - Chart.js Visualizations
 */

class DashboardChartsManager {
    constructor() {
        this.disasterTypeChart = null;
        this.requestStatusChart = null;
        this.resourceChart = null;
    }

    async renderCharts() {
        const stats = await api.getDashboardStats();
        this.renderDisasterTypeChart(stats.disasterTypeCounts);
        this.renderRequestStatusChart(stats.requestStatusCounts);
        this.renderResourceChart(stats.resourceCategoryQuantities);
    }

    renderDisasterTypeChart(typeCounts) {
        const ctx = document.getElementById('chart-disaster-types');
        if (!ctx) return;

        if (this.disasterTypeChart) {
            this.disasterTypeChart.destroy();
        }

        const labels = Object.keys(typeCounts || {});
        const data = Object.values(typeCounts || {});

        const backgroundColors = [
            '#2563eb', // FLOOD - Blue
            '#78350f', // LANDSLIDE - Brown
            '#b45309', // EARTHQUAKE - Amber
            '#7c3aed', // CYCLONE - Purple
            '#ea580c', // FIRE - Orange
            '#06b6d4', // TSUNAMI - Cyan
            '#64748b'  // OTHER - Slate
        ];

        this.disasterTypeChart = new Chart(ctx, {
            type: 'doughnut',
            data: {
                labels: labels,
                datasets: [{
                    data: data,
                    backgroundColor: backgroundColors,
                    borderWidth: 2,
                    borderColor: '#ffffff'
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        position: 'bottom',
                        labels: {
                            boxWidth: 12,
                            padding: 10,
                            font: { size: 11 }
                        }
                    }
                },
                cutout: '65%'
            }
        });
    }

    renderRequestStatusChart(statusCounts) {
        const ctx = document.getElementById('chart-request-statuses');
        if (!ctx) return;

        if (this.requestStatusChart) {
            this.requestStatusChart.destroy();
        }

        const labels = Object.keys(statusCounts || {});
        const data = Object.values(statusCounts || {});

        this.requestStatusChart = new Chart(ctx, {
            type: 'bar',
            data: {
                labels: labels,
                datasets: [{
                    label: 'Requests',
                    data: data,
                    backgroundColor: [
                        '#f59e0b', // PENDING - Amber
                        '#3b82f6', // ASSIGNED - Blue
                        '#6366f1', // IN_PROGRESS - Indigo
                        '#10b981', // FULFILLED - Green
                        '#ef4444'  // CANCELLED - Red
                    ],
                    borderRadius: 6
                }]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: { display: false }
                },
                scales: {
                    y: {
                        beginAtZero: true,
                        ticks: { stepSize: 1 }
                    }
                }
            }
        });
    }

    renderResourceChart(resourceQuantities) {
        const ctx = document.getElementById('chart-resources');
        if (!ctx) return;

        if (this.resourceChart) {
            this.resourceChart.destroy();
        }

        const labels = Object.keys(resourceQuantities || {});
        const data = Object.values(resourceQuantities || {});

        this.resourceChart = new Chart(ctx, {
            type: 'bar',
            data: {
                labels: labels,
                datasets: [{
                    label: 'Units in Stock',
                    data: data,
                    backgroundColor: '#1e3a8a',
                    borderRadius: 6
                }]
            },
            options: {
                indexAxis: 'y',
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: { display: false }
                },
                scales: {
                    x: {
                        beginAtZero: true
                    }
                }
            }
        });
    }
}

const chartsManager = new DashboardChartsManager();
