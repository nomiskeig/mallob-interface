import { configParameters } from './Parameters';

function getParamByInternalName(name) {
	let param = configParameters.find((param) => (param.internalName === name));
    console.log(param);
    return param;

}
test('Priority checks value', () => {
	let prio = getParamByInternalName('priority');
	expect(prio.validateValue('not a number')).toEqual(
		expect.objectContaining({ isValid: false })
	);
	expect(prio.validateValue(-1)).toEqual(
		expect.objectContaining({ isValid: false })
	);
});

test("MaxDemand checks value", () => {
  let maxDemand = getParamByInternalName('maxDemand');
    console.log(maxDemand)
 expect(maxDemand.validateValue(-1)).toEqual(expect.objectContaining({isValid: false}))
 expect(maxDemand.validateValue("not a number")).toEqual(expect.objectContaining({isValid: false}))
})

test("Application checks value", ()=>{
    let application = getParamByInternalName('application');
    expect(application.validateValue(undefined)).toEqual(expect.objectContaining({isValid: false}))
})

test('Params with no specal validation return true', () => {
	let paramsToCheck = configParameters.filter(
		(param) =>
			param.internalName !== 'priority' &&
			param.internalName !== 'application' &&
			param.internalName !== 'maxDemand' &&
			param.internalName !== 'wallclockLimit' &&
			param.internalName !== 'cpuLimit' &&
            param.internalName !== 'precursor' &&
            param.internalName !== 'name'
	);
    paramsToCheck.forEach((param) => {
        expect(param.validateValue("value")).toEqual(expect.objectContaining({isValid: true}))
    })
});

test('Params where output is not transformed return given value', ()=>{
    let paramsToCheck = configParameters.filter((param) => param.internalName !== "additionalConfig");
    let value = "value to check";
    paramsToCheck.forEach((param) => {
        expect(param.transformOutput(value)).toEqual(value);
    })
})
