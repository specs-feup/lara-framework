const sampleAst = `Joinpoint 'program'
    Joinpoint 'file'
        Joinpoint 'include'
        Joinpoint 'include'
        Joinpoint 'include'
        Joinpoint 'comment'
        Joinpoint 'comment'
        Joinpoint 'function'
            Joinpoint 'param'
            Joinpoint 'param'
            Joinpoint 'param'
            Joinpoint 'param'
            Joinpoint 'param'
            Joinpoint 'param'
            Joinpoint 'body'
                Joinpoint 'loop'
                Joinpoint 'declStmt'
                    Joinpoint 'vardecl'
                        Joinpoint 'intLiteral'
                Joinpoint 'exprStmt'
                    Joinpoint 'binaryOp'
                        Joinpoint 'varref'
                        Joinpoint 'varref'
                Joinpoint 'exprStmt'
                    Joinpoint 'unaryOp'
                        Joinpoint 'varref'
                Joinpoint 'body'
                    Joinpoint 'loop'
                        Joinpoint 'declStmt'
                            Joinpoint 'vardecl'
                            Joinpoint 'intLiteral'
                        Joinpoint 'exprStmt'
                            Joinpoint 'binaryOp'
                            Joinpoint 'varref'
                            Joinpoint 'varref'
                        Joinpoint 'exprStmt'
                            Joinpoint 'unaryOp'
                            Joinpoint 'varref'
                        Joinpoint 'body'
                            Joinpoint 'wrapperStmt'
                            Joinpoint 'comment'
                            Joinpoint 'exprStmt'
                            Joinpoint 'binaryOp'
                                Joinpoint 'arrayAccess'
                                    Joinpoint 'varref'
                                    Joinpoint 'binaryOp'
                                        Joinpoint 'binaryOp'
                                        Joinpoint 'varref'
                                        Joinpoint 'varref'
                                        Joinpoint 'varref'
                                Joinpoint 'intLiteral'
                Joinpoint 'loop'
                Joinpoint 'declStmt'
                    Joinpoint 'vardecl'
                        Joinpoint 'intLiteral'
                Joinpoint 'exprStmt'
                    Joinpoint 'binaryOp'
                        Joinpoint 'varref'
                        Joinpoint 'varref'
                Joinpoint 'exprStmt'
                    Joinpoint 'unaryOp'
                        Joinpoint 'varref'
                Joinpoint 'body'
                    Joinpoint 'loop'
                        Joinpoint 'declStmt'
                            Joinpoint 'vardecl'
                            Joinpoint 'intLiteral'
                        Joinpoint 'exprStmt'
                            Joinpoint 'binaryOp'
                            Joinpoint 'varref'
                            Joinpoint 'varref'
                        Joinpoint 'exprStmt'
                            Joinpoint 'unaryOp'
                            Joinpoint 'varref'
                        Joinpoint 'body'
                            Joinpoint 'loop'
                            Joinpoint 'declStmt'
                                Joinpoint 'vardecl'
                                    Joinpoint 'intLiteral'
                            Joinpoint 'exprStmt'
                                Joinpoint 'binaryOp'
                                    Joinpoint 'varref'
                                    Joinpoint 'varref'
                            Joinpoint 'exprStmt'
                                Joinpoint 'unaryOp'
                                    Joinpoint 'varref'
                            Joinpoint 'body'
                                Joinpoint 'wrapperStmt'
                                    Joinpoint 'comment'
                                Joinpoint 'exprStmt'
                                    Joinpoint 'binaryOp'
                                        Joinpoint 'arrayAccess'
                                        Joinpoint 'varref'
                                        Joinpoint 'binaryOp'
                                            Joinpoint 'binaryOp'
                                                Joinpoint 'varref'
                                                Joinpoint 'varref'
                                            Joinpoint 'varref'
                                        Joinpoint 'binaryOp'
                                        Joinpoint 'arrayAccess'
                                            Joinpoint 'varref'
                                            Joinpoint 'binaryOp'
                                                Joinpoint 'binaryOp'
                                                    Joinpoint 'varref'
                                                    Joinpoint 'varref'
                                                Joinpoint 'varref'
                                        Joinpoint 'arrayAccess'
                                            Joinpoint 'varref'
                                            Joinpoint 'binaryOp'
                                                Joinpoint 'binaryOp'
                                                    Joinpoint 'varref'
                                                    Joinpoint 'varref'
                                                Joinpoint 'varref'
        Joinpoint 'comment'
        Joinpoint 'function'
            Joinpoint 'param'
            Joinpoint 'param'
            Joinpoint 'param'
            Joinpoint 'body'
                Joinpoint 'loop'
                Joinpoint 'declStmt'
                    Joinpoint 'vardecl'
                        Joinpoint 'intLiteral'
                Joinpoint 'exprStmt'
                    Joinpoint 'binaryOp'
                        Joinpoint 'varref'
                        Joinpoint 'varref'
                Joinpoint 'exprStmt'
                    Joinpoint 'unaryOp'
                        Joinpoint 'varref'
                Joinpoint 'body'
                    Joinpoint 'loop'
                        Joinpoint 'declStmt'
                            Joinpoint 'vardecl'
                            Joinpoint 'intLiteral'
                        Joinpoint 'exprStmt'
                            Joinpoint 'binaryOp'
                            Joinpoint 'varref'
                            Joinpoint 'varref'
                        Joinpoint 'exprStmt'
                            Joinpoint 'unaryOp'
                            Joinpoint 'varref'
                        Joinpoint 'body'
                            Joinpoint 'wrapperStmt'
                            Joinpoint 'comment'
                            Joinpoint 'exprStmt'
                            Joinpoint 'binaryOp'
                                Joinpoint 'arrayAccess'
                                    Joinpoint 'varref'
                                    Joinpoint 'binaryOp'
                                        Joinpoint 'binaryOp'
                                        Joinpoint 'varref'
                                        Joinpoint 'varref'
                                        Joinpoint 'varref'
                                Joinpoint 'binaryOp'
                                    Joinpoint 'parenExpr'
                                        Joinpoint 'cast'
                                        Joinpoint 'call'
                                            Joinpoint 'varref'
                                    Joinpoint 'cast'
                                        Joinpoint 'intLiteral'
        Joinpoint 'function'
            Joinpoint 'param'
            Joinpoint 'param'
            Joinpoint 'param'
            Joinpoint 'body'
                Joinpoint 'declStmt'
                Joinpoint 'vardecl'
                    Joinpoint 'floatLiteral'
                Joinpoint 'if'
                Joinpoint 'binaryOp'
                    Joinpoint 'varref'
                    Joinpoint 'intLiteral'
                Joinpoint 'body'
                    Joinpoint 'exprStmt'
                        Joinpoint 'call'
                            Joinpoint 'varref'
                            Joinpoint 'varref'
                            Joinpoint 'binaryOp'
                            Joinpoint 'varref'
                            Joinpoint 'intLiteral'
                            Joinpoint 'varref'
                Joinpoint 'loop'
                Joinpoint 'declStmt'
                    Joinpoint 'vardecl'
                        Joinpoint 'intLiteral'
                Joinpoint 'exprStmt'
                    Joinpoint 'binaryOp'
                        Joinpoint 'varref'
                        Joinpoint 'varref'
                Joinpoint 'exprStmt'
                    Joinpoint 'unaryOp'
                        Joinpoint 'varref'
                Joinpoint 'body'
                    Joinpoint 'loop'
                        Joinpoint 'declStmt'
                            Joinpoint 'vardecl'
                            Joinpoint 'intLiteral'
                        Joinpoint 'exprStmt'
                            Joinpoint 'binaryOp'
                            Joinpoint 'varref'
                            Joinpoint 'varref'
                        Joinpoint 'exprStmt'
                            Joinpoint 'unaryOp'
                            Joinpoint 'varref'
                        Joinpoint 'body'
                            Joinpoint 'wrapperStmt'
                            Joinpoint 'comment'
                            Joinpoint 'exprStmt'
                            Joinpoint 'binaryOp'
                                Joinpoint 'varref'
                                Joinpoint 'arrayAccess'
                                    Joinpoint 'varref'
                                    Joinpoint 'binaryOp'
                                        Joinpoint 'binaryOp'
                                        Joinpoint 'varref'
                                        Joinpoint 'varref'
                                        Joinpoint 'varref'
                Joinpoint 'exprStmt'
                Joinpoint 'call'
                    Joinpoint 'varref'
                    Joinpoint 'literal'
                    Joinpoint 'varref'
        Joinpoint 'function'
            Joinpoint 'body'
                Joinpoint 'declStmt'
                Joinpoint 'vardecl'
                    Joinpoint 'intLiteral'
                Joinpoint 'declStmt'
                Joinpoint 'vardecl'
                    Joinpoint 'intLiteral'
                Joinpoint 'declStmt'
                Joinpoint 'vardecl'
                    Joinpoint 'intLiteral'
                Joinpoint 'wrapperStmt'
                Joinpoint 'comment'
                Joinpoint 'wrapperStmt'
                Joinpoint 'comment'
                Joinpoint 'wrapperStmt'
                Joinpoint 'comment'
                Joinpoint 'declStmt'
                Joinpoint 'vardecl'
                    Joinpoint 'cast'
                        Joinpoint 'call'
                            Joinpoint 'varref'
                            Joinpoint 'binaryOp'
                            Joinpoint 'binaryOp'
                                Joinpoint 'varref'
                                Joinpoint 'varref'
                            Joinpoint 'unaryExprOrType'
                Joinpoint 'declStmt'
                Joinpoint 'vardecl'
                    Joinpoint 'cast'
                        Joinpoint 'call'
                            Joinpoint 'varref'
                            Joinpoint 'binaryOp'
                            Joinpoint 'binaryOp'
                                Joinpoint 'varref'
                                Joinpoint 'varref'
                            Joinpoint 'unaryExprOrType'
                Joinpoint 'declStmt'
                Joinpoint 'vardecl'
                    Joinpoint 'cast'
                        Joinpoint 'call'
                            Joinpoint 'varref'
                            Joinpoint 'binaryOp'
                            Joinpoint 'binaryOp'
                                Joinpoint 'varref'
                                Joinpoint 'varref'
                            Joinpoint 'unaryExprOrType'
                Joinpoint 'wrapperStmt'
                Joinpoint 'comment'
                Joinpoint 'exprStmt'
                Joinpoint 'call'
                    Joinpoint 'varref'
                    Joinpoint 'varref'
                    Joinpoint 'varref'
                    Joinpoint 'varref'
                Joinpoint 'exprStmt'
                Joinpoint 'call'
                    Joinpoint 'varref'
                    Joinpoint 'varref'
                    Joinpoint 'varref'
                    Joinpoint 'varref'
                Joinpoint 'wrapperStmt'
                Joinpoint 'comment'
                Joinpoint 'exprStmt'
                Joinpoint 'call'
                    Joinpoint 'varref'
                    Joinpoint 'varref'
                    Joinpoint 'varref'
                    Joinpoint 'varref'
                    Joinpoint 'varref'
                    Joinpoint 'varref'
                    Joinpoint 'varref'
                Joinpoint 'exprStmt'
                Joinpoint 'call'
                    Joinpoint 'varref'
                    Joinpoint 'varref'
                    Joinpoint 'varref'
                    Joinpoint 'varref'
        Joinpoint 'function'
            Joinpoint 'body'
                Joinpoint 'wrapperStmt'
                Joinpoint 'comment'
                Joinpoint 'exprStmt'
                Joinpoint 'call'
                    Joinpoint 'varref'
                    Joinpoint 'intLiteral'
                Joinpoint 'exprStmt'
                Joinpoint 'call'
                    Joinpoint 'varref'`;  // TODO: Import from Lara

const fillAstContainer = (ast) => {
    const astContainer = document.querySelector('#ast code');

    for (const node of ast.split('\n')) {
        const matches = node.match(/(\s*)Joinpoint '(.+)'/, '');
        if (matches == null) {
            console.warn(`Invalid node: "${node}"`);
            continue;
        }
        const [, indentation, nodeName] = matches;
        
        const nodeElement = document.createElement('span');
        nodeElement.classList.add('ast-node');  // TODO: Add joinpoint info
        nodeElement.textContent = nodeName;

        astContainer.innerHTML += indentation + ' - ';
        astContainer.appendChild(nodeElement);
        astContainer.appendChild(document.createElement('br'));
    }
}

fillAstContainer(sampleAst);