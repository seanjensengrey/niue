;; Copyright 2010 Vijay Mathew Pandyalakal. All rights reserved.

;; Redistribution and use in source and binary forms, with or 
;; without modification, are permitted provided that the following 
;; conditions are met:

;;    1. Redistributions of source code must retain the above copyright 
;;       notice, this list of conditions and the following disclaimer.

;;    2. Redistributions in binary form must reproduce the above copyright 
;;       notice, this list of conditions and the following disclaimer in the 
;;       documentation and/or other materials provided with the distribution.

;; THIS SOFTWARE IS PROVIDED BY VIJAY MATHEW PANDYALAKAL ``AS IS'' AND ANY 
;; EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED 
;; WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
;; DISCLAIMED. IN NO EVENT SHALL VIJAY MATHEW PANDYALAKAL OR CONTRIBUTORS BE 
;; LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
;; CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
;; SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
;; INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
;; CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
;; ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF 
;; THE POSSIBILITY OF SUCH DAMAGE.

;; Niue major mode for Emacs.

;; the command to comment/uncomment text
(defun niue-comment-dwim (arg)
  "Comment or uncomment current line or region in a smart way.
For detail, see `comment-dwim'."
  (interactive "*P")
  (require 'newcomment)
  (let ((deactivate-mark nil) (comment-start "( ") (comment-end " )"))
    (comment-dwim arg)))

;; All the highlighted 'keywords' has equal status amoung themselves and
;; with user-defined words. 
(defvar core-words '("mod" "/mod" "equals" "and" "or" "not"
		     "swap" "swap-at" "dup" "over" "rot"
		     "drop" "2swap" "2dup" "2over" "2drop"
		     "send" "recv" "len"
		     "str-len" "str-at" "str-eq" "str-eqi"
		     "str-tolower" "str-toupper" "str-trim"
		     "str-replace" "str-replace-all" "substring" "str-find"
		     "at" "remove" "remove-all" "remove-if" "replace"
		     "replace-all" "get" "set" "reverse" "bsearch" "sort")
  "Core operations")
(defvar vm-words '(".s" "emit" "newline" "space" "." ".ns" "," ";" ";;" 
		   "forget" "!" "!!" "sleep" "self" "super" "load" "eval"
		   "true" "false" ".clr" "if" "elif" "else" "when" "unless"
		   "while" "times" "times-by")
  "VM operations")
(defvar core-words-regexp (regexp-opt core-words 'words))
(defvar vm-words-regexp (regexp-opt vm-words 'words))
(setq core-words nil)
(setq vm-words nil)

(setq niue-keywords 
      `(( ,core-words-regexp . font-lock-function-name-face)
	( ,vm-words-regexp . font-lock-constant-face)))

(define-derived-mode niue-mode fundamental-mode
  (setq font-lock-defaults '(niue-keywords))
  
  ;; modify the keymap
  (define-key niue-mode-map [remap comment-dwim] 'niue-comment-dwim)

  ;; comment: "( ... )"
  (modify-syntax-entry ?\( ". 1" niue-mode-syntax-table)
  (modify-syntax-entry ?\) ". 4" niue-mode-syntax-table)
  (modify-syntax-entry ?\s ". 23" niue-mode-syntax-table)

  (setq indent-line-function 'lisp-indent-line)
  (setq mode-name "Niue"))

(provide 'niue)
