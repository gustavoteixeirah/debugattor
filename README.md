# Sistema de Debug da Pipeline

### Rastreamento do Processo

    O sistema deve permitir o início de um novo "processamento" para cada imagem que entra na pipeline, tratando-o como um caso único.

    Cada processamento deve receber um identificador único para que possa ser rastreado do início ao fim.

    O sistema deve armazenar a imagem original que deu início ao processamento.

### Registro das Etapas (Timeline)

    Para cada processamento, o sistema deve permitir o registro sequencial de "etapas" ou "passos" da pipeline.

    Cada etapa registrada deve conter os artefatos de debug correspondentes àquele ponto do processo.

    O sistema precisa ser flexível para aceitar diferentes tipos de artefatos de debug em cada etapa:

        Uma imagem (o resultado visual da etapa).

        Dados estruturados (métricas, scores, coordenadas, etc.).

        Texto (logs, observações).

        Uma combinação de diferentes formatos (ex: uma imagem de debug junto com um JSON de métricas).

### Visualização e Análise

    O sistema deve apresentar uma visão geral de todos os processamentos já executados (uma lista de todas as imagens que passaram pela pipeline).

    Ao selecionar um processamento específico, a interface deve exibir sua timeline completa, mostrando todas as etapas e seus respectivos artefatos de debug em ordem cronológica.

    O usuário deve conseguir inspecionar facilmente os artefatos de cada etapa (visualizar a imagem, ler o texto, examinar os dados).

    Deve ser possível buscar ou filtrar processamentos específicos, por exemplo, por data, por status (sucesso/falha) ou por um identificador da imagem original.# debugattor
