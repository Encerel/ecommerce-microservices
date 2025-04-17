{{- define "shared.service" }}
apiVersion: v1
kind: Service
metadata:
  name: {{ .Chart.Name }}
  labels:
    helm.sh/chart: {{ .Chart.Name }}
    app.kubernetes.io/managed-by: {{ $.Release.Service }}
    app.kubernetes.io/name: {{ .Chart.Name }}
spec:
  selector:
    app: {{ .Chart.Name }}
  ports:
  {{- if .Values.service.ports }}
    {{- range .Values.service.ports }}
    - name: {{ .name }}
      port: {{ .port }}
      targetPort: {{ .targetPort }}
    {{- end }}
  {{- else }}
    - protocol: TCP
      port: {{ .Values.app.container.port }}
      targetPort: {{ .Values.app.container.targetPort }}
  {{- end }}
  type: {{ .Values.service.type }}
{{- end }}