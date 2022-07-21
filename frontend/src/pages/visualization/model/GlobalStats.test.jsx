import {GlobalStats} from './GlobalStats';

test('sets the active jobs correctly', () => {
    let globalStats = new GlobalStats();
    globalStats.setActiveJobs(5);
    expect(globalStats.getActiveJobs()).toBe(5);
})

test('sets the processes correctly', () => {
    let globalStats = new GlobalStats();
    globalStats.setProcesses(5);
    expect(globalStats.getProcesses()).toBe(5);
})

test('sets the used processes correctly', () => {
    let globalStats = new GlobalStats();
    globalStats.setUsedProcesses(5);
    expect(globalStats.getUsedProcesses()).toBe(5);
})
