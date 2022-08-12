import {useContext} from 'react';
import {UserContext} from '../../context/UserContextProvider'
import {useNavigate} from 'react-router-dom';
import './AdminPage.scss'
import { Button } from '../../global/buttons/Button';


export function AdminPage(props) {
    //let userContext = useContext(UserContext)
    //let navigate = useNavigate();

    /**
     * This function has to actually gather the warning.
     * @param {*} i 
     * @returns all warnings from API as array or null, if no warnings available
     */
    function getWarnings(){
        return ["warning 1", "warning 2", "warning 3", "warning 3", "warning 3", "warning 3", "warning 3", "warning 3", "warning 3"];
        return null; //if no warnings available 
    }

    function createWarning(warning, i){
        if (i % 2 === 1){
            return(
                <li className="warningsElement warningsElementOne">{warning}</li>
            );
        }
        return(
            <li className="warningsElement warningsElementTwo">{warning}</li>
        );

    }

    function getIndividualWarnings(){
        const items = [];
        const warnings = getWarnings();
        if (warnings == null){
            return(createWarning("No warnings available", 0));
        }
        for (let i = 0; i < warnings.length; i++){
            items.push(createWarning(warnings[i], i));
        }
        return items;
    }

    function getWarningsAsList(){
        return(
            <ul className="navbar">
                {getIndividualWarnings()}
            </ul>
        );
    }

    function getWarningsElement(){
        return(
            <nav className="navbar">
                {getWarningsAsList()}
            </nav>
        );
    }

    function startMallob(){
        //not yet implemented
    }

    function stopMallob(){
        //not yet implemented
    }

    function restartMallob(){
        //not yet implemented
    }

    function getMallobButtons(){
        return(
            <div>
                <div>
                <Button onClick={startMallob} color="#A5C9CA" text="Start Mallob" className="mallobButton"></Button>
                </div>
                <div>
                <Button onClick={stopMallob} text="Stop Mallob" className="mallobButton"></Button>

                </div>
                <div>
                <Button onClick={restartMallob} text="Restart Mallob" className="mallobButton"></Button>

                </div>
            </div>
        );
    }

    function getContainerTitle(title){
        return(
            <div className="diagnosticsTitle">
                <h3>{title}</h3>
                <hr className="separator"></hr>
            </div>
        );
    }

    function getDiagnosticsContainer(title, content, specialClassName){
        if (specialClassName == null){
            //return ordinary diagnostics container
            return(
                <div className="diagnosticsContainer">
                    {getContainerTitle(title)}
                    {content}
                </div>
            );
        }
        return(
            <div className={specialClassName}>
                {getContainerTitle(title)}
                {content}
            </div>
        );
    }

	return (
		<div className="py-5 registerPage">
            <div className="pageContent">
                {getDiagnosticsContainer("Warnings", getWarningsElement(), "warnings")}
                {getDiagnosticsContainer("Mallob", getMallobButtons(), "mallobButtons")}
            </div>

        </div>
	);
}
