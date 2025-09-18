<html>
<#-- @ftlvariable name="data" type="guru.qa.niffler.data.logging.SqlAttachmentData" -->
<head>
    <meta http-equiv="content-type" content="text/html; charset = UTF-8">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.9.0/styles/atom-one-dark.min.css">
    <script src="https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.9.0/highlight.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.9.0/languages/sql.min.js"></script>

    <style>
        pre {
            white-space: pre-wrap;
            padding: 12px;
            background: #282c34;
            border-radius: 6px;
        }
        /* Кастомные стили */
        .hljs-keyword { /* INSERT, INTO, VALUES */
            color: #569cd6 !important;
        }
        .hljs-literal,  /* true/false */
        .hljs-built_in { /* NULL */
            color: #ff6363 !important;
        }
        .hljs-string { /* Строки */
            color: #ce9178 !important;
        }
    </style>
    <script>
        document.addEventListener('DOMContentLoaded', () => {
            hljs.highlightAll();
        });
    </script>
</head>
<body>
<h5 style="margin-bottom: 8px;">SQL Query</h5>
<pre><code class="language-sql">${data.sql}</code></pre>
</body>
</html>